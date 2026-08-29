import { useState } from "react";

export default function SystemModal({onClose, onSaved, system, planets, token})
{
    const [loading, setLoading] = useState(false),
        [error, setError] = useState(false);

    const isEditing = Boolean(system);

    async function handleSubmit(e)
    {
        e.preventDefault();

        const formData = new FormData(e.target);

        const systemPayload = {
            name: formData.get("name"),
            starType: formData.get("starType"),
            starMass: Number(formData.get("starMass")),
            distanceFromEarth: Number(formData.get("distanceFromEarth")),
            description: formData.get("description"),
            publicSystem: formData.get("publicSystem") === true,
        };
        const url = isEditing? `/api/star-systems/${system.id}` : "/api/star-systems",
            method = isEditing ? "PUT" : "POST";

        setLoading(true);

        try {
            const data = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}` },
                body: JSON.stringify(systemPayload)
            }),
                returnJSON = data.ok ? await data.json() : null;
            
            if(returnJSON) onSaved(returnJSON);
            else setError(true);
        } catch (err) {
            console.error(err);
            setError(true);
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
                            <h5 id="systemModalTitle" className="modal-title">
                                {isEditing? "Edit System" : "Create System"}
                            </h5>
                            <button type="button" onClick={onClose} disabled={loading}
                                className="btn-close"/>
                        </div>
                        <div className="modal-body">
                            <div className="row g-3">
                                <div className="col-md-6">
                                    <label htmlFor="systemName" className="form-label">Name</label>
                                    <input id="systemName" name="name" defaultValue={system?.name || ""}
                                        className="form-control"
                                        required />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="systemStarType" className="form-label">Type</label>
                                    <input id="systemStarType" name="starType" defaultValue={system?.type || ""}
                                        className="form-control"
                                        required />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="systemStarMass" className="form-label">
                                        Star mass, relative to the Sun
                                    </label>
                                    <input id="systemStarMass" name="starMass" type="number" min="0" step="any"
                                        defaultValue={system?.mass || ""} className="form-control"
                                        required />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="systemDistanceFromEarth" className="form-label">
                                        Distance from Earth in Lightyears
                                    </label>
                                    <input id="systemDistanceFromEarth" name="distanceFromEarth" type="number"
                                        min="0.01" step="any" defaultValue={system?.distanceFromEarth || ""}
                                        className="form-control"
                                        required />
                                </div>
                                <div className="col-6">
                                    <div className="form-check">
                                        <input id="systemPublic" name="publicSystem" type="checkbox"
                                            defaultChecked={system?.publicSystem ?? false} className="form-check-input" />
                                        <label htmlFor="systemPublic" className="form-check-label">
                                            Make this system public
                                        </label>
                                    </div>
                                </div>
                                <div className="col-12">
                                    <label htmlFor="systemDescription" className="form-label">
                                        Description
                                    </label>
                                    <textarea id="systemDescription" name="description" rows="3"
                                        defaultValue={system?.description || ""} className="form-control" />
                                </div>
                                {isEditing && (planets?.length ?
                                    <div className="col-12">
                                        <label htmlFor="systemPlanets" className="form-label">
                                            Planets
                                        </label>
                                        <div>{planets?.join(", ")}</div>
                                    </div> : <p className="text-muted">No known planets</p>)
                                }
                            </div>
                        </div>
                        <div className="modal-footer">
                            {error ? <p className="text-danger">Something went wrong!</p> : <></>}
                            {isEditing?
                                <label htmlFor="newPlanetUsername" className="form-label text-dark">
                                    New Username:
                                </label> : <></>
                            }
                            <input type="text" name="newUsername" id="newPlanetUsername" />
                            <button type="button" onClick={onClose} disabled={loading}
                                className="btn btn-secondary bg-danger">
                                Cancel
                            </button>
                            <button type="submit" disabled={loading}
                                className="btn btn-success bg-stars">
                                {!loading?
                                    isEditing? "Save Changes" : "Create System" :
                                    isEditing? "Saving" : "Creating..."
                                }
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}