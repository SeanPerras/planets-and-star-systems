const React = require("react"),
    renderer = require("react-test-renderer"),
    { act } = renderer,
    PlanetModal = require("../PlanetModal").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

const systemDesigs = [
    { ID: 10, name: "Solar System" },
    { ID: 20, name: "Alpha Centauri" }
];

describe("PlanetModal", () =>
{
    test("renders create mode with system options", () =>
    {
        const component = renderer.create(
            React.createElement(PlanetModal, {
                onClose: jest.fn(),
                onSaved: jest.fn(),
                systemDesigs,
                token: "mock-token"
            })
        );

        const root = component.root,
            text = getText(root);

        expect(text).toContain("Create Planet");

        const systemSelect = root.findByProps({
            id: "planetSystemID",
        }),
            options = systemSelect.findAllByType("option");

        expect(options).toHaveLength(3);
        expect(options[0].children.join("")).toBe("Rogue");
        expect(options[1].children.join("")).toBe("Solar System");
        expect(options[2].children.join("")).toBe("Alpha Centauri");
    });

    test("renders edit mode with existing planet information", () =>
    {
        const component = renderer.create(
            React.createElement(PlanetModal, {
                onClose: jest.fn(),
                onSaved: jest.fn(),
                systemDesigs,
                token: "mock-token",
                planet: {
                    id: 1,
                    name: "Earth",
                    type: "Terrestrial",
                    mass: 1,
                    distanceFromStar: 1,
                    moons: '["Moon"]',
                    description: "Earth, our home planet.",
                    publicPlanet: true,
                    systemID: 10
                }
            })
        );

        const root = component.root,
            text = getText(root);

        expect(text).toContain("Edit Planet");

        expect(
            root.findByProps({
                id: "planetName",
            }).props.defaultValue
        ).toBe("Earth");

        expect(
            root.findByProps({
                id: "planetType",
            }).props.defaultValue
        ).toBe("Terrestrial");

        expect(
            root.findByProps({
                id: "planetSystemID",
            }).props.defaultValue
        ).toBe(10);
    });

    test("submits a new planet and calls onSaved with the API result", async () =>
    {
        const onSaved = jest.fn(),
            createdPlanet = {
                id: 99,
                name: "Test Planet",
                type: "Terrestrial",
                mass: 1,
                distanceFromStar: 1,
                moons: '["Test Moon"]',
                systemID: 10,
            };

        global.fetch = jest.fn().mockResolvedValue({
            ok: true,
            json: jest.fn().mockResolvedValue(createdPlanet),
        });

        const originalFormData = global.FormData;

        global.FormData = jest.fn(() => ({
            get: jest.fn((fieldName) =>
            {
                const values = {
                    name: "Test Planet",
                    type: "Terrestrial",
                    mass: "1",
                    distanceFromStar: "1",
                    description: "A test-created planet.",
                    moons: "Test Moon",
                    publicPlanet: "on",
                    planetSystemID: "10",
                };

                return values[fieldName] ?? null;
            }),
        }));

        const component = renderer.create(
            React.createElement(PlanetModal, {
                onClose: jest.fn(),
                onSaved,
                systemDesigs: [
                    {
                        ID: 10,
                        name: "Solar System",
                    },
                ],
                token: "mock-token",
            })
        ),
            root = component.root,
            form = root.findByType("form");

        await act(async () =>
        {
            await form.props.onSubmit({
                preventDefault: jest.fn(),
                target: {},
            });
        });

        expect(global.fetch).toHaveBeenCalledTimes(1);

        expect(global.fetch).toHaveBeenCalledWith(
            "/api/planets",
            expect.objectContaining({
                method: "POST",
                headers: expect.objectContaining({
                    Authorization: "Bearer mock-token",
                }),
            })
        );

        expect(onSaved).toHaveBeenCalledTimes(1);
        expect(onSaved).toHaveBeenCalledWith(createdPlanet);

        global.FormData = originalFormData;
    });
});