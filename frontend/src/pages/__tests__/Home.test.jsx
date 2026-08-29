const React = require("react"),
    renderer = require("react-test-renderer"),
    Home = require("../Home").default;

function getText(element)
{
    if (typeof element === "string") return element;
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

describe("Home Component", () =>
{
    test("renders the main heading", () =>
    {
        const component = renderer.create(React.createElement(Home)),
            root = component.root,
            heading = root.findByType("h1");

        expect(heading.children.join("")).toBe("Planets and Star Systems");
        expect(heading.props.className).toContain("fw-bold");
        expect(heading.props.className).toContain("text-white-50");
    });

    test("renders the introductory paragraph", () =>
    {
        const component = renderer.create(React.createElement(Home)),
            root = component.root,
            paragraph = root.findByType("p"),
            text = getText(paragraph);

        expect(text).toContain("Space is my favorite thing");
        expect(text).toContain("spaghettified");
        expect(text).toContain("Rogue Planet");
    });

    test('renders the "spaghettified" link', () =>
    {
        const component = renderer.create(React.createElement(Home)),
            root = component.root,
            link = root.findAllByType("a").find((item) =>
                item.children.join("").includes("spaghettified")
            );

        expect(link).toBeDefined();
        expect(link.props.href).toBe(
            "https://science.nasa.gov/universe/what-happens-when-something-gets-too-close-to-a-black-hole/"
        );
    });

    test("renders the three footer links", () =>
    {
        const component = renderer.create(React.createElement(Home)),
            root = component.root,
            links = root.findAllByType("a"),
            localPlanetsLink = links.find(
                (link) =>
                    link.props.href === "https://science.nasa.gov/solar-system/planets/"
            ),
            exoplanetsLink = links.find(
                (link) =>
                    link.props.href ===
                    "https://science.nasa.gov/exoplanets/exoplanet-catalog/"
            ),
            cssPatternsLink = links.find(
                (link) =>
                    link.props.href ===
                    "https://projects.verou.me/css3patterns/#starry-night"
            );

        expect(localPlanetsLink).toBeDefined();
        expect(getText(localPlanetsLink)).toContain("our local");

        expect(exoplanetsLink).toBeDefined();
        expect(getText(exoplanetsLink)).toContain("real exoplanets");

        expect(cssPatternsLink).toBeDefined();
        expect(getText(cssPatternsLink)).toBe("here");
    });

    test("renders the home container with expected classes", () =>
    {
        const component = renderer.create(React.createElement(Home)),
            root = component.root,
            home = root.findByProps({ id: "home" });

        expect(home.props.className).toContain("container");
        expect(home.props.className).toContain("mt-4");
    });
});