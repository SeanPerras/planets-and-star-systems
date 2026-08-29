const React = require("react"),
    renderer = require("react-test-renderer"),
    StarSystem = require("../StarSystem").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

describe("StarSystem component", () =>
{
    test("renders star system details and its planets", () =>
    {
        const component = renderer.create(
            React.createElement(StarSystem, {
                name: "The Solar System",
                starType: "Main-Sequence",
                starMass: 1,
                distanceFromEarth: 0,
                planetNames: ["Mercury", "Venus", "Earth", "Mars"]
            })
        ),
            text = getText(component.root);

        expect(text).toContain("The Solar System");
        expect(text).toContain("Star Type:");
        expect(text).toContain("Main-Sequence");
        expect(text).toContain("Star Mass:");
        expect(text).toContain("1x the Sun");
        expect(text).toContain("Distance from Earth:");
        expect(text).toContain("N/A");
        expect(text).toContain("Planets:");
        expect(text).toContain("Mercury, Venus, Earth, Mars");
    });

    test("shows fallback values when system details and planets are unavailable", () =>
    {
        const component = renderer.create(
            React.createElement(StarSystem, {
                name: "Lonely System",
                starType: "Unknown",
                starMass: "1.5",
                distanceFromEarth: "25000000",
                planetNames: []
            })
        ),
            text = getText(component.root);

        expect(text).toContain("Lonely System");
        expect(text).toContain("Unknown");
        expect(text).toContain("No known planets");
    });

    test("shows no known planets when planetNames is omitted", () =>
    {
        const component = renderer.create(
            React.createElement(StarSystem, {
                name: "Planetless System",
                starType: "Dwarf",
                starMass: 0.2,
                distanceFromEarth: 12.5
            })
        ),
            text = getText(component.root);

        expect(text).toContain("Planetless System");
        expect(text).toContain("No known planets");
    });
});