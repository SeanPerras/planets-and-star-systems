import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import PlanetModal from "../components/modals/PlanetModal";

export default function PlanetDetails({currentUser, getToken})
{
    const location = useLocation();
    const { ID } = useParams();
    
    const [planet, setPlanet] = useState(location.state?.planet),
        [systemDesigs, setSystemDesigs] = useState(location.state?.systemDesigs ?? []),
        [loading, setLoading] = useState(false),
        [isEditVisible, setEditVisible] = useState(false);

    const systemNames = Object.fromEntries(
        (systemDesigs || []).map(sd => [sd.ID, sd.name])
    );

    const canEdit = currentUser === planet?.ownerUsername;

    useEffect(() => {
        if(!planet)
            loadPlanet();
    }, [currentUser]);

    async function loadPlanet()
    {
        setLoading(true);

        try {
            const header = currentUser ? { Authorization: `Bearer ${getToken()}` } : {},
                [data, designatorData] = await Promise.all([
                    fetch(`/api/planets/${ID}`, { headers: header }),
                    fetch(`/api/star-systems/designators`, { headers: header })
                ]),
                [planetJSON, designatorJSON] = await Promise.all([
                    data.ok ? systemResponse.json() : null,
                    designatorData.ok ? planetsResponse.json() : null
                ]);

            setPlanet(planetJSON);
            setSystemDesigs(designatorJSON);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    }

    return (
        <main className="container mt-4 text-light">
            <div className="row justify-content-between">
                <Link to="/planets" className="btn btn-outline-light border-0 mb-3 col-2">
                    ← Back to Planets
                </Link>
                {canEdit &&
                    <button type="button" onClick={() => setEditVisible(true)} className="btn btn-outline-warning mb-3 col-2">
                        Edit Planet
                    </button>
                }
            </div>
            {!planet? <p className="text-danger">Something went wrong!</p> :
                loading? <p>Loading planet...</p> :
                <article className="card bg-dark bg-opacity-75 text-light shadow">
                    <div className="card-body p-4">
                        <h1 className="card-title">
                            {planet.name} - <i className="fw-normal text-white-50">
                                {systemNames[planet.systemID] ?? "Rogue"}</i>
                        </h1>
                        <div className="row g-3 align-items-center">
                            {[["Type:", planet.type, ""],
                            ["Mass:", planet.mass, "x Earth"],
                            ["Distance from star:", planet.distanceFromStar, " AU"]]
                            .map(([stat, value, units]) =>
                                <div key={stat} className="col-md-4">
                                    <b className="text-uppercase">{stat} </b>
                                    {value? value + units : "N/A"}
                                </div>
                            )}
                            <div className="col-md-12">
                                <b className="text-uppercase">Moons: </b> {planet.moons === "[]" ?
                                    "No known moons." : JSON.parse(planet.moons).join(", ")}
                            </div>
                        </div>
                        <hr />
                        <details name="Description" className="lead">
                            <summary>Description</summary>
                            {planet.description}
                        </details>
                    </div>
                </article>
            }
            {isEditVisible &&
                <PlanetModal planet={planet} systemDesigs={systemDesigs} token={getToken()}
                    onClose={() => setEditVisible(false)}
                    onSaved={updatedPlanet => {
                        setPlanet(updatedPlanet);
                        setEditVisible(false);
                    }}
                />
            }
        </main>
    );
}