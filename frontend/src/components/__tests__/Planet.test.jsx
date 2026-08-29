const React = require("react"),
    renderer = require("react-test-renderer"),
    Planet = require("../Planet").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

describe("Planet component", () =>
{
    test("renders a planet's name, system, details, and moons", () =>
    {
        const component = renderer.create(
            React.createElement(Planet, {
                name: "Earth",
                type: "Terrestrial",
                mass: 1,
                distanceFromStar: 1,
                moons: '["The Moon"]',
                systemName: "Solar System",
            })
        );

        const text = getText(component.root);

        expect(text).toContain("Earth");
        expect(text).toContain("Solar System");
        expect(text).toContain("Type:");
        expect(text).toContain("Terrestrial");
        expect(text).toContain("Mass:");
        expect(text).toContain("1x Earth");
        expect(text).toContain("Distance:");
        expect(text).toContain("1 AU");
        expect(text).toContain("Moons:");
        expect(text).toContain("The Moon");
    });

    test("shows Rogue and no known moons when system and moon data are absent", () =>
    {
        const component = renderer.create(
            React.createElement(Planet, {
                name: "Rogue One",
                type: "Rogue Planet",
                mass: null,
                distanceFromStar: null,
                moons: null,
                systemName: null,
            })
        );

        const text = getText(component.root);

        expect(text).toContain("Rogue One");
        expect(text).toContain("Rogue");
        expect(text).toContain("N/A");
        expect(text).toContain("No known moons.");
    });

    test("formats a short list of multiple moons", () =>
    {
        const component = renderer.create(
            React.createElement(Planet, {
                name: "Mars",
                type: "Terrestrial",
                mass: 0.107,
                distanceFromStar: 1.52,
                moons: '["Phobos", "Deimos"]',
                systemName: "Solar System",
            })
        );

        const text = getText(component.root);

        expect(text).toContain("Phobos, Deimos");
    });
});