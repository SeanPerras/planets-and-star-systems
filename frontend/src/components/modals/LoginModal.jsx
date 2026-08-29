import { useState } from "react";

export default function LoginModal({onClose, onLogin})
{
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function handleSubmit(e)
    {
        e.preventDefault();

        const formData = new FormData(e.target);

        const user = {
            username: formData.get("username"),
            password: formData.get("password")
        };

        setLoading(true);
        setError("");

        try {
            const data = await fetch("/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(user)
            }),
                loginJSON = data.ok ? await data.json() : null,
                token = loginJSON?.accessToken.token;
            
            onLogin(token, user.username);
        } catch (err) {
            console.error(err);
            setError("Could not create the planet.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="modal d-block" tabIndex="-1">
            <div className="modal-dialog modal-lg">
                <div className="modal-content bg-dark-subtle">
                    <form onSubmit={handleSubmit}>
                        <div className="modal-header bg-secondary">
                            <h5 id="loginTitle" className="modal-title">Login</h5>
                            <button type="button" onClick={onClose} disabled={loading}
                                className="btn-close"/>
                        </div>
                        <div className="modal-body">
                            <div className="column g-3">
                                <div className="col-md-6">
                                    <label htmlFor="loginUsername" className="form-label">Username</label>
                                    <input id="loginUsername" name="username"
                                        className="form-control"
                                        required/>
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="loginPassword" className="form-label">Password</label>
                                    <input id="loginPassword" name="password" type="password"
                                        className="form-control"
                                        required/>
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" onClick={onClose} disabled={loading}
                                className="btn btn-secondary bg-danger">
                                Cancel
                            </button>
                            <button type="submit" disabled={loading}
                                className="btn btn-success bg-stars">
                                {!loading ? "Login" : "Logging in..."}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}