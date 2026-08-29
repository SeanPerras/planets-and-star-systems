const React = require("react"),
    renderer = require("react-test-renderer"),
    { act } = renderer,
    SystemModal = require("../SystemModal").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

describe("SystemModal", () =>
{
    test("renders create mode", () =>
    {
        const component = renderer.create(
            React.createElement(SystemModal, {
                onClose: jest.fn(),
                onSaved: jest.fn(),
                planets: [],
                token: "mock-token"
            })
        ),
            root = component.root,
            text = getText(root);

        expect(text).toContain("Create System");

        expect(
            root.findByProps({ id: "systemName" })
        ).toBeDefined();

        expect(
            root.findByProps({ id: "systemStarType" })
        ).toBeDefined();

        expect(
            root.findByProps({ id: "systemStarMass" })
        ).toBeDefined();

        expect(
            root.findByProps({ id: "systemDistanceFromEarth" })
        ).toBeDefined();
    });

    test("renders edit mode and displays its planet names", () =>
    {
        const component = renderer.create(
            React.createElement(SystemModal, {
                onClose: jest.fn(),
                onSaved: jest.fn(),
                token: "mock-token",
                system: {
                    id: 10,
                    name: "Solar System",
                    starType: "Main-Sequence",
                    starMass: 1,
                    distanceFromEarth: 0,
                    description: "Our star system.",
                    publicSystem: true
                },
                planets: ["Mercury", "Venus", "Earth", "Mars"]
            })
        ),
            text = getText(component.root);

        expect(text).toContain("Edit System");
        expect(text).toContain("Mercury, Venus, Earth, Mars");
    });

    test("submits a new system and calls onSaved with the API result", async () =>
    {
        const onSaved = jest.fn(),
            createdSystem = {
                id: 99,
                name: "Test System",
                starType: "Main-Sequence",
                starMass: 1,
                distanceFromEarth: 10,
            };

        global.fetch = jest.fn().mockResolvedValue({
            ok: true,
            json: jest.fn().mockResolvedValue(createdSystem),
        });

        const originalFormData = global.FormData;

        global.FormData = jest.fn(() => ({
            get: jest.fn((fieldName) =>
            {
                const values = {
                    name: "Test System",
                    starType: "G-type",
                    starMass: "1",
                    distanceFromEarth: "10",
                    description: "A test-created star system.",
                    publicSystem: "on",
                };

                return values[fieldName] ?? null;
            }),
        }));

        const component = renderer.create(
            React.createElement(SystemModal, {
                onClose: jest.fn(),
                onSaved,
                planets: [],
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
            "/api/star-systems",
            expect.objectContaining({
                method: "POST",
                headers: expect.objectContaining({
                    Authorization: "Bearer mock-token",
                }),
            })
        );

        expect(onSaved).toHaveBeenCalledTimes(1);
        expect(onSaved).toHaveBeenCalledWith(createdSystem);

        global.FormData = originalFormData;
    });
});