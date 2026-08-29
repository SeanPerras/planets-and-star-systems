const React = require("react"),
    renderer = require("react-test-renderer"),
    { MemoryRouter, Routes, Route } = require("react-router-dom"),
    StarSystemDetails = require("../StarSystemDetails").default;

jest.mock("../../components/modals/SystemModal", () =>
{
    return function MockSystemModal()
    {
        return null;
    };
});

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

function renderStarSystemDetails({ starSystem, planets = [], currentUser = null })
{
    return renderer.create(
        React.createElement(
            MemoryRouter,
            {
                initialEntries: [
                    {
                        pathname: "/star-systems/10",
                        state: {
                            starSystem,
                            planets
                        }
                    }
                ]
            },
            React.createElement(
                Routes,
                null,
                React.createElement(Route, {
                    path: "/star-systems/:ID",
                    element: React.createElement(StarSystemDetails, {
                        currentUser,
                        getToken: jest.fn(() => "test-token")
                    })
                })
            )
        )
    );
}

describe("StarSystemDetails page", () =>
{
    test("renders system details and names of its planets", () =>
    {
        const component = renderStarSystemDetails({
            starSystem: {
                ID: 10,
                name: "Solar System",
                starType: "Main-Sequence",
                starMass: 1,
                distanceFromEarth: 0,
                description: "Our home star system.",
                ownerUsername: "owner"
            },
            planets: [
                {
                    id: 1,
                    name: "Earth",
                    systemID: 10
                },
                {
                    id: 2,
                    name: "Mars",
                    systemID: 10
                }
            ]
        }),
            text = getText(component.root);

        expect(text).toContain("Back to Star Systems");
        expect(text).toContain("Solar System");
        expect(text).toContain("Main-Sequence");
        expect(text).toContain("1x the Sun");
        expect(text).toContain("N/A");
        expect(text).toContain("Earth, Mars");
        expect(text).toContain("Our home star system.");
    });

    test("shows fallback values and no planets when no planet list exists", () =>
    {
        const component = renderStarSystemDetails({
            starSystem: {
                ID: 20,
                name: "Lonely System",
                starType: "Main-Sequence",
                starMass: "1",
                distanceFromEarth: null,
                description: "No planets have been discovered here.",
                ownerUsername: "owner"
            },
            planets: []
        }),
            text = getText(component.root);

        expect(text).toContain("Lonely System");
        expect(text).toContain("N/A");
        expect(text).toContain("No known planets");
        expect(text).toContain("No planets have been discovered here.");
    });

    test("shows Edit System button only when the current user owns the system", () =>
    {
        const component = renderStarSystemDetails({
            currentUser: "owner",
            starSystem: {
                ID: 10,
                name: "Solar System",
                starType: "Main-Sequence",
                starMass: 1,
                distanceFromEarth: 0,
                description: "Our home star system.",
                ownerUsername: "owner"
            }
        }),
            text = getText(component.root),
            root = component.root,
            editButton = root.findAllByType("button").find((button) =>
                getText(button).includes("Edit System")
            );

        expect(editButton).toBeDefined();
        expect(editButton.props.type).toBe("button");
        expect(editButton.props.className).toContain("btn-outline-warning");
        expect(typeof editButton.props.onClick).toBe("function");
        expect(text).toContain("Edit System");
    });
});