import { useState } from "react";

export default function PlanetModal({onClose, onSaved, systemDesigs, planet, token})
{
    const [rogue, setRogue] = useState(planet?.systemID === null),
        [loading, setLoading] = useState(false),
        [error, setError] = useState(false);

    const isEditing = Boolean(planet);

    async function handleSubmit(e)
    {
        e.preventDefault();

        const formData = new FormData(e.target);

        const planetPayload = {
            name: formData.get("name"),
            type: formData.get("type"),
            mass: Number(formData.get("mass")),
            distanceFromStar: rogue? null :
                Number(formData.get("distanceFromStar")),
            description: formData.get("description"),
            moons: JSON.stringify(
                formData.get("moons").split(",").map(m => m.trim())
                    .filter(m => m.length > 0)),
            publicPlanet: formData.get("publicPlanet") === true,
            systemID: rogue? null : Number(formData.get("planetSystemID"))
        };
        const url = isEditing? `/api/planets/${planet.id}` : "/api/planets",
            method = isEditing ? "PUT" : "POST";

        setLoading(true);

        try {
            const data = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}` },
                body: JSON.stringify(planetPayload)
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
                            <h5 id="planetModalTitle" className="modal-title">
                                {isEditing? "Edit Planet" : "Create Planet"}
                            </h5>
                            <button type="button" onClick={onClose} disabled={loading}
                                className="btn-close"/>
                        </div>
                        <div className="modal-body">
                            <div className="row g-3">
                                <div className="col-md-6">
                                    <label htmlFor="planetName" className="form-label text-dark">Name</label>
                                    <input id="planetName" name="name" defaultValue={planet?.name || ""}
                                        className="form-control"
                                        required />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="planetType" className="form-label text-dark">Type</label>
                                    <input id="planetType" name="type" defaultValue={planet?.type || ""}
                                        className="form-control"
                                        required />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="planetMass" className="form-label text-dark">
                                        Mass, relative to Earth
                                    </label>
                                    <input id="planetMass" name="mass" type="number" min="0" step="any"
                                        defaultValue={planet?.mass || ""} className="form-control"
                                        required />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="planetDistanceFromStar" className="form-label text-dark">
                                        Distance from star in Astronomical Units
                                    </label>
                                    <input id="planetDistanceFromStar" name="distanceFromStar" type="number"
                                        min="0.01" step="any" defaultValue={planet?.distanceFromStar || ""}
                                        className="form-control"
                                        disabled={rogue}
                                        required={!rogue}
                                    />
                                </div>
                                <div className="col-6">
                                    <div className="form-check">
                                        <input id="planetRogue" type="checkbox" checked={rogue}
                                            onChange={(e) => setRogue(e.target.checked)}
                                            className="form-check-input" />
                                        <label htmlFor="planetRogue" className="form-check-label text-dark">
                                            Rogue planet
                                        </label>
                                    </div>
                                </div>
                                <div className="col-6">
                                    <div className="form-check">
                                        <input id="planetPublic" name="publicPlanet" type="checkbox"
                                            defaultChecked={planet?.publicPlanet ?? false} className="form-check-input" />
                                        <label htmlFor="planetPublic" className="form-check-label text-dark">
                                            Make this planet public
                                        </label>
                                    </div>
                                </div>
                                <div className="col-12">
                                    <label htmlFor="planetMoons" className="form-label text-dark">
                                        Moons
                                    </label>
                                    <input id="planetMoons" name="moons"
                                        defaultValue={planet?.moons? JSON.parse(planet.moons).join(", ") : ""}
                                        placeholder='Comma separated list, e.g. "Moon One, Moon Two"'
                                        className="form-control" />
                                </div>
                                <div className="col-12">
                                    <label htmlFor="planetDescription" className="form-label text-dark">
                                        Description
                                    </label>
                                    <textarea id="planetDescription" name="description" rows="3"
                                        defaultValue={planet?.description || ""} className="form-control" />
                                </div>
                                <div className="col-12">
                                    <label htmlFor="planetSystemID" className="form-label text-dark">System:</label>
                                    <select name="planetSystemID" id="planetSystemID" disabled={rogue}
                                        defaultValue={planet?.systemID ?? ""}
                                        className="form-select">
                                        <option value="">Rogue</option>
                                        {systemDesigs?.map(sys =>
                                            <option key={sys.ID + sys.name} value={sys.ID}>{sys.name}</option>
                                        )}
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            {error ? <p className="text-danger">Something went wrong!</p> : <></>}
                            <label htmlFor="newPlanetUsername" className="form-label text-dark">
                                {isEditing? "New " : ""} Username:
                            </label>
                            <input type="text" name="newUsername" id="newPlanetUsername" />
                            <button type="button" onClick={onClose} disabled={loading}
                                className="btn btn-secondary bg-danger">
                                Cancel
                            </button>
                            <button type="submit" disabled={loading}
                                className="btn btn-success bg-stars">
                                {!loading?
                                    isEditing? "Save Changes" : "Create Planet" :
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