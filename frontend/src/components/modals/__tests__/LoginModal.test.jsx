const React = require("react"),
    renderer = require("react-test-renderer"),
    LoginModal = require("../LoginModal").default;

function getText(element)
{
    if (typeof element === "string" || typeof element === "number")
        return String(element);
    if (Array.isArray(element)) return element.map(getText).join("");
    if (element && element.children) return element.children.map(getText).join("");
    return "";
}

describe("LoginModal", () =>
{
    test("renders login fields and a submit button", () =>
    {
        const component = renderer.create(
            React.createElement(LoginModal, {
                onClose: jest.fn(),
                onLogin: jest.fn()
            })
        );

        const root = component.root,
            text = getText(root);

        expect(text).toContain("Login");

        const username = root.findByProps({
            id: "loginUsername",
        });

        const password = root.findByProps({
            id: "loginPassword",
        });

        expect(username.props.name).toBe("username");
        expect(password.props.name).toBe("password");
        expect(password.props.type).toBe("password");

        const submitButton = root.findAllByType("button").find((button) =>
            getText(button).includes("Login")
        );

        expect(submitButton).toBeDefined();
        expect(submitButton.props.type).toBe("submit");
    });

    test("calls onClose when Cancel is clicked", () =>
    {
        const onClose = jest.fn(),
            component = renderer.create(
                React.createElement(LoginModal, {
                    onClose,
                    onLogin: jest.fn(),
                })
            ),
            root = component.root,
            cancelButton = root.findAllByType("button").find((button) =>
                getText(button).includes("Cancel")
            );

        cancelButton.props.onClick();

        expect(onClose).toHaveBeenCalledTimes(1);
    });

    test("calls onLogin with the returned token and submitted username", async () =>
    {
        const onLogin = jest.fn();

        global.fetch = jest.fn().mockResolvedValue({
            ok: true,
            json: jest.fn().mockResolvedValue({
                accessToken: {
                    token: "mock-token",
                },
            }),
        });

        const originalFormData = global.FormData;

        global.FormData = jest.fn(() => ({
            get: jest.fn((fieldName) =>
            {
                const values = {
                    username: "test-user",
                    password: "secret-password",
                };

                return values[fieldName];
            }),
        }));

        const component = renderer.create(
            React.createElement(LoginModal, {
                onClose: jest.fn(),
                onLogin,
            })
        );

        const root = component.root,
            form = root.findByType("form");

        await renderer.act(async () =>
        {
            await form.props.onSubmit({
                preventDefault: jest.fn(),
                target: {}
            });
        });

        expect(global.fetch).toHaveBeenCalledWith(
            "/auth/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: "test-user",
                    password: "secret-password"
                })
            }
        );

        expect(onLogin).toHaveBeenCalledTimes(1);

        expect(onLogin).toHaveBeenCalledWith(
            "mock-token",
            "test-user"
        );

        global.FormData = originalFormData;
    });
});