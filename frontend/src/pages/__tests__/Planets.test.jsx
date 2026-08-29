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

jest.mock("../../components/Planet", () =>
{
    return function MockPlanet({ ID, name, systemName })
    {
        return mockReact.createElement(
            "article",
            {
                "mockID": "planet-card",
                "planet-ID": ID,
                "system-name": systemName
            },
            name
        );
    };
});

jest.mock("../../components/modals/PlanetModal", () =>
{
    return function MockPlanetModal({ onClose, onSaved, systemDesigs, token })
    {
        return mockReact.createElement(
            "section",
            {
                "mockID": "planet-modal",
                "system-count": systemDesigs?.length ?? 0,
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
                            ID: 99,
                            name: "Test Planet",
                            mass: 1,
                            distanceFromStar: 1,
                            moons: "[]",
                            systemID: 1
                        }),
                },
                "Save mock planet"
            )
        );
    };
});

const Planets = require("../Planets").default;

const planets = [
    {
        ID: 1,
        name: "Earth",
        type: "Terrestrial",
        mass: 1,
        distanceFromStar: 1,
        moons: "[]",
        systemID: 10,
    },
    {
        ID: 2,
        name: "Rogue One",
        type: "Rogue Planet",
        mass: 2,
        distanceFromStar: null,
        moons: "[]",
        systemID: null,
    }
],
    systems = [
        { ID: 10, name: "Solar System" }
    ];

function response(json, ok = true)
{
    return { ok, json: jest.fn().mockResolvedValue(json) };
}

async function renderPlanets(getToken = jest.fn(() => "test-token"))
{
    let component;

    await act(async () =>
    {
        component = renderer.create(
            React.createElement(
                MemoryRouter,
                null,
                React.createElement(Planets, { getToken })
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

describe("Planets page", () =>
{
    test("loads planets and system designators when mounted", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(planets))
            .mockResolvedValueOnce(response(systems));

        const component = await renderPlanets(),
            root = component.root;

        expect(global.fetch).toHaveBeenCalledTimes(2);

        expect(global.fetch).toHaveBeenNthCalledWith(
            1,
            "/api/planets",
            {headers: {}}
        );

        expect(global.fetch).toHaveBeenNthCalledWith(
            2,
            "/api/star-systems/designators",
            {headers: {}}
        );

        const cards = root.findAllByProps({ "mockID": "planet-card" });

        expect(cards).toHaveLength(2);
        expect(cards[0].children.join("")).toBe("Earth");
        expect(cards[0].props["system-name"]).toBe("Solar System");
        expect(cards[1].children.join("")).toBe("Rogue One");
    });

    test("shows an error message when the initial load fails", async () =>
    {
        global.fetch.mockRejectedValue(new Error("Network failure"));

        const component = await renderPlanets(),
            root = component.root,
            error = root.findByProps({ className: "text-danger" });

        expect(error.children.join("")).toBe("Whoops, something went wrong!");
    });

    test("renders system options returned by the API", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(planets))
            .mockResolvedValueOnce(response(systems));

        const component = await renderPlanets(),
            root = component.root,
            select = root.findByProps({ id: "systemID" }),
            options = select.findAllByType("option");

        expect(options).toHaveLength(2);
        expect(options[0].children.join("")).toBe("All Systems");
        expect(options[1].children.join("")).toBe("Solar System");
        expect(options[1].props.value).toBe(10);
    });

    test("disables rogue-planet options after selecting a system", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(planets))
            .mockResolvedValueOnce(response(systems));

        const component = await renderPlanets(),
            root = component.root,
            select = root.findByProps({ id: "systemID" });

        await act(async () =>
        {
            select.props.onChange({ target: { value: "10" } });
        });

        const updatedRoot = component.root,
            rogueOnly = updatedRoot.findByProps({ id: "rogueOnly" }),
            rogueAll = updatedRoot.findByProps({ id: "rogueAll" }),
            rogueNone = updatedRoot.findByProps({ id: "rogueNone" });

        expect(rogueOnly.props.disabled).toBe(true);
        expect(rogueAll.props.disabled).toBe(true);
        expect(rogueNone.props.disabled).toBe(true);
    });

    test("opens the create-planet modal when its button is clicked", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(planets))
            .mockResolvedValueOnce(response(systems));

        const component = await renderPlanets(),
            root = component.root,
            createButton = root.findAllByType("button").find((button) =>
                button.children.join("").includes("Create Your Own Planet!")
            );

        await act(async () =>
        {
            createButton.props.onClick();
        });

        const updatedRoot = component.root,
            modal = updatedRoot.findByProps({ "mockID": "planet-modal" });

        expect(modal.props["system-count"]).toBe(1);
        expect(modal.props["token"]).toBe("test-token");
    });

    test("adds a created planet and closes the modal after saving", async () =>
    {
        global.fetch
            .mockResolvedValueOnce(response(planets))
            .mockResolvedValueOnce(response(systems));

        const component = await renderPlanets();
        expect(component).toBeDefined();
        let root = component.root;

        const createButton = root.findAllByType("button").find((button) =>
            button.children.join("").includes("Create Your Own Planet!")
        );
        expect(createButton).toBeDefined();

        await act(async () =>
        {
            createButton.props.onClick();
        });

        root = component.root;

        const saveButton = root.findAllByType("button").find((button) =>
            button.children.join("").includes("Save mock planet")
        );
        expect(saveButton).toBeDefined();

        await act(async () =>
        {
            saveButton.props.onClick();
        });

        root = component.root;

        expect(
            root.findAllByProps({ "mockID": "planet-modal" })
        ).toHaveLength(0);

        const cards = root.findAllByProps({ "mockID": "planet-card" });

        expect(cards).toHaveLength(3);
        expect(cards[2].children.join("")).toBe("Test Planet");
    });
});