const React = require("react"),
    renderer = require("react-test-renderer"),
    { act } = renderer,
    { MemoryRouter } = require("react-router-dom"),
    mockReact = React;

jest.mock("../pages/Home", () =>
{
    return function MockHome()
    {
        return mockReact.createElement(
            "main",
            { "mockid": "home-page" },
            "Mock Home Page"
        );
    };
});

jest.mock("../pages/StarSystems", () =>
{
    return function MockStarSystems()
    {
        return mockReact.createElement(
            "main",
            { "mockid": "star-systems-page" },
            "Mock Star Systems Page"
        );
    };
});

jest.mock("../pages/StarSystemDetails", () =>
{
    return function MockStarSystemDetails()
    {
        return mockReact.createElement(
            "main",
            { "mockid": "star-system-details-page" },
            "Mock Star System Details Page"
        );
    };
});

jest.mock("../pages/Planets", () =>
{
    return function MockPlanets()
    {
        return mockReact.createElement(
            "main",
            { "mockid": "planets-page" },
            "Mock Planets Page"
        );
    };
});

jest.mock("../pages/PlanetDetails", () =>
{
    return function MockPlanetDetails()
    {
        return mockReact.createElement(
            "main",
            { "mockid": "planet-details-page" },
            "Mock Planet Details Page"
        );
    };
});

jest.mock("../components/modals/LoginModal", () =>
{
    return function MockLoginModal({ onClose, onLogin })
    {
        return mockReact.createElement(
            "section",
            {
                "mockid": "login-modal",
            },
            mockReact.createElement(
                "button",
                {
                    type: "button",
                    onClick: onClose,
                },
                "Close mock login modal"
            ),
            mockReact.createElement(
                "button",
                {
                    type: "button",
                    onClick: () => onLogin("mock-token", "mock-user"),
                },
                "Complete mock login"
            )
        );
    };
});

const App = require("../App").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

function renderApp(path = "/")
{
    return renderer.create(
        React.createElement(
            MemoryRouter,
            {
                initialEntries: [path],
            },
            React.createElement(App)
        )
    );
}

describe("App", () =>
{
    test("renders navigation links and the Home route", () =>
    {
        const component = renderApp("/"),
            root = component.root,
            links = root.findAllByType("a");

        expect(links).toHaveLength(3);

        expect(getText(links[0])).toBe("Home");
        expect(links[0].props.href).toBe("/");

        expect(getText(links[1])).toBe("Star Systems");
        expect(links[1].props.href).toBe("/star-systems");

        expect(getText(links[2])).toBe("Planets");
        expect(links[2].props.href).toBe("/planets");

        expect(
            root.findByProps({
                "mockid": "home-page",
            })
        ).toBeDefined();

        const loginButton = root.findAllByType("button").find((button) =>
            getText(button).includes("Login")
        );

        expect(loginButton).toBeDefined();
        expect(loginButton.props.type).toBe("button");
    });

    test("opens LoginModal after the Login button is clicked", async () =>
    {
        const component = renderApp("/");
        let root = component.root;

        expect(
            root.findAllByProps({
                "mockid": "login-modal",
            })
        ).toHaveLength(0);

        const loginButton = root.findAllByType("button").find((button) =>
            getText(button).includes("Login")
        );

        await act(async () =>
        {
            loginButton.props.onClick();
        });

        root = component.root;

        const modal = root.findByProps({
            "mockid": "login-modal",
        });

        expect(modal).toBeDefined();
    });

    test("renders the Planets route at /planets", () =>
    {
        const component = renderApp("/planets"),
            root = component.root;

        expect(
            root.findByProps({
                "mockid": "planets-page",
            })
        ).toBeDefined();
    });
});