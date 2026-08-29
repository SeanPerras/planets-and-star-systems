const React = require("react"),
    renderer = require("react-test-renderer"),
    { act } = renderer,
    { MemoryRouter } = require("react-router-dom"),
    mockReact = React;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

jest.mock("../../components/StarSystem", () =>
{
    return function MockStarSystem({ id, name, planetNames })
    {
        return mockReact.createElement(
            "article",
            {
                "mockID": "star-system-card",
                "system-ID": id,
                "planet-names": planetNames?.join(", ") ?? ""
            },
            name
        );
    };
});

jest.mock("../../components/modals/SystemModal", () =>
{
    return function MockSystemModal({ onClose, onSaved, planets, token })
    {
        return mockReact.createElement(
            "section",
            {
                "mockID": "system-modal",
                "planet-count": planets?.length ?? 0,
                "token": token
            },
            mockReact.createElement(
                "button",
                { type: "button", onClick: onClose },
                "Close mock modal"
            ),
            mockReact.createElement(
                "button",
                {
                    type: "button",
                    onClick: () =>
                        onSaved({
                            id: 99,
                            name: "Mock System",
                            mass: 1,
                            distanceFromStar: 1,
                            moons: "[]",
                            systemID: 1
                        }),
                },
                "Save mock system"
            )
        );
    };
});

const StarSystems = require("../StarSystems").default,
    starSystems = [
        { id: 1, name: "The Solar System", starType: "Main-Sequence" },
        { id: 2, name: "Alpha Centauri", starType: "Main-Sequence" }
    ],
    planets = [
        { id: 1, name: "Earth", systemID: 1 },
        { id: 2, name: "Mars", systemID: 1 },
        { id: 3, name: "Proxima b", systemID: 2 }
    ];

function response(json, ok = true)
{
    return { ok, json: jest.fn().mockResolvedValue(json) };
}

async function renderStarSystems(getToken = jest.fn(() => "mock-token"))
{
    let component;

    await act(async () =>
    {
        component = renderer.create(
            React.createElement(
                MemoryRouter,
                null,
                React.createElement(StarSystems, { currentUser: null, getToken })
            )
        );

        await Promise.resolve();
    });

    return component;
}

beforeEach(() =>
{
    global.fetch = jest.fn();
});

afterEach(() =>
{
    jest.clearAllMocks();
});

describe("StarSystems page", () =>
{
    test("loads and renders star systems with their matching planets", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(starSystems))
            .mockResolvedValueOnce(response(planets));

        const getToken = jest.fn(() => "mock-token"),
            component = await renderStarSystems(getToken),
            root = component.root;

        expect(global.fetch).toHaveBeenCalledTimes(2);

        expect(global.fetch).toHaveBeenNthCalledWith(
            1,
            "/api/star-systems",
            { headers: {} }
        );

        expect(global.fetch).toHaveBeenNthCalledWith(
            2,
            "/api/planets",
            { headers: {} }
        );

        expect(getToken).toHaveBeenCalledTimes(0);

        const cards = root.findAllByProps({ "mockID": "star-system-card" });

        expect(cards).toHaveLength(2);

        expect(cards[0].children.join("")).toBe("The Solar System");
        expect(cards[0].props["planet-names"]).toBe("Earth, Mars");

        expect(cards[1].children.join("")).toBe("Alpha Centauri");
        expect(cards[1].props["planet-names"]).toBe("Proxima b");
    });

    test("shows an error message when page data fails to load", async () =>
    {
        global.fetch.mockRejectedValue(new Error("Network failure"));

        const component = await renderStarSystems(),
            root = component.root,
            error = root.findByProps({ className: "text-danger" });

        expect(error.children.join("")).toBe("Whoops, something went wrong!");
    });

    test("opens the create-system modal when its button is clicked", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(starSystems))
            .mockResolvedValueOnce(response(planets));

        const component = await renderStarSystems();
        let root = component.root;

        const createButton = root.findAllByType("button").find((button) =>
            button.children.join("").includes("Create Your Own Star System!")
        );

        expect(createButton).toBeDefined();

        await act(async () =>
        {
            createButton.props.onClick();
        });

        root = component.root;

        const modal = root.findByProps({ "mockID": "system-modal" });

        expect(modal).toBeDefined();
        expect(modal.props["planet-count"]).toBe(3);
        expect(modal.props["token"]).toBe("mock-token");
    });

    test("adds a saved star system and closes the modal", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(starSystems))
            .mockResolvedValueOnce(response(planets));

        const component = await renderStarSystems();
        let root = component.root;

        const createButton = root.findAllByType("button").find((button) =>
            button.children.join("").includes("Create Your Own Star System!")
        );

        await act(async () =>
        {
            createButton.props.onClick();
        });

        root = component.root;

        const saveButton = root.findAllByType("button").find((button) =>
            button.children.join("").includes("Save mock system")
        );

        expect(saveButton).toBeDefined();

        await act(async () =>
        {
            saveButton.props.onClick();
        });

        root = component.root;

        expect(
            root.findAllByProps({ "mockID": "system-modal" })
        ).toHaveLength(0);

        const cards = root.findAllByProps({ "mockID": "star-system-card" });

        expect(cards).toHaveLength(3);
        expect(cards[2].children.join("")).toBe("Mock System");
    });

    test("searches star systems and displays a singular result count", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(starSystems))
            .mockResolvedValueOnce(response(planets))
            .mockResolvedValueOnce(
                response([
                    {
                        id: 1,
                        name: "The Solar System",
                        starType: "Main-Sequence"
                    }
                ])
            );

        const originalFormData = global.FormData;

        global.FormData = jest.fn(() => ({
            entries: () => [
                ["name", "The Sol"],
                ["starType", ""],
                ["planetCount", ""]
            ]
        }));

        const component = await renderStarSystems();
        let root = component.root;

        const form = root.findByType("form");

        await act(async () =>
        {
            form.props.onSubmit({
                preventDefault: jest.fn(),
                target: {}
            });

            await Promise.resolve();
        });

        root = component.root;

        expect(global.fetch).toHaveBeenCalledTimes(3);

        expect(global.fetch).toHaveBeenLastCalledWith(
            "/api/star-systems?name=The+Sol",
            { headers: {} }
        );

        const text = getText(root);

        expect(text).toContain("1 Star System Found");

        global.FormData = originalFormData;
    });
});