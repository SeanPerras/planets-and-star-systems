const React = require("react"),
    renderer = require("react-test-renderer"),
    { MemoryRouter, Routes, Route } = require("react-router-dom");

jest.mock("../../components/modals/PlanetModal", () =>
{
    return function MockPlanetModal()
    {
        return null;
    };
});

const PlanetDetails = require("../PlanetDetails").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

function renderPlanetDetails({ planet, systemDesigs = [], currentUser = null })
{
    return renderer.create(
        React.createElement(
            MemoryRouter,
            {
                initialEntries: [
                    {
                        pathname: "/planets/1",
                        state: { planet, systemDesigs }
                    }
                ]
            },
            React.createElement(
                Routes,
                null,
                React.createElement(Route, {
                    path: "/planets/:ID",
                    element: React.createElement(PlanetDetails, {
                        currentUser,
                        getToken: jest.fn(() => "test-token"),
                    })
                })
            )
        )
    );
}

describe("PlanetDetails page", () =>
{
    test("renders planet details and its related system name", () =>
    {
        const component = renderPlanetDetails({
            planet: {
                id: 1,
                name: "Earth",
                type: "Terrestrial",
                mass: 1,
                distanceFromStar: 1,
                moons: '["Moon"]',
                description: "Our home planet.",
                ownerUsername: "owner",
                systemID: 10
            },
            systemDesigs: [
                { ID: 10, name: "Solar System" }
            ]
        });

        const text = getText(component.root);

        expect(text).toContain("Back to Planets");
        expect(text).toContain("Earth");
        expect(text).toContain("Solar System");
        expect(text).toContain("Terrestrial");
        expect(text).toContain("1x Earth");
        expect(text).toContain("1 AU");
        expect(text).toContain("Moon");
        expect(text).toContain("Our home planet.");
    });

    test("renders Rogue, N/A values, and no known moons when data is missing", () =>
    {
        const component = renderPlanetDetails({
            planet: {
                id: 2,
                name: "Rogue One",
                type: "Rogue Planet",
                mass: null,
                distanceFromStar: null,
                moons: "[]",
                description: "A planet without a home system.",
                ownerUsername: "owner",
                systemID: null,
            }
        });

        const text = getText(component.root);

        expect(text).toContain("Rogue One");
        expect(text).toContain("Rogue");
        expect(text).toContain("N/A");
        expect(text).toContain("No known moons.");
        expect(text).toContain("A planet without a home system.");
    });

    test("shows Edit Planet button only when the current user owns the planet", () =>
    {
        const component = renderPlanetDetails({
            currentUser: "owner",
            planet: {
                id: 1,
                name: "Earth",
                type: "Terrestrial",
                mass: 1,
                distanceFromStar: 1,
                moons: '["Moon"]',
                description: "Earth is our home planet.",
                ownerUsername: "owner",
                systemID: 10,
            },
            systemDesigs: [
                { ID: 10, name: "Solar System" }
            ]
        });

        const text = getText(component.root),
            root = component.root,
            editButton = root.findAllByType("button").find((button) =>
                getText(button).includes("Edit Planet")
            );

        expect(editButton).toBeDefined();
        expect(editButton.props.type).toBe("button");
        expect(editButton.props.className).toContain("btn-outline-warning");
        expect(typeof editButton.props.onClick).toBe("function");
        expect(text).toContain("Edit Planet");
    });
});