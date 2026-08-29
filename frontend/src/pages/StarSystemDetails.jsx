import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import SystemModal from "../components/modals/SystemModal";

export default function StarSystemDetails({currentUser, getToken})
{
    const location = useLocation();
    const { ID } = useParams();

    const [starSystem, setStarSystem] = useState(location.state?.starSystem),
        [planets, setPlanets] = useState(location.state?.planets),
        [loading, setLoading] = useState(false),
        [isEditVisible, setEditVisible] = useState(false);

    const planetNames = planets?.filter(p => p.systemID == ID).map(p => p.name),
        canEdit = currentUser === starSystem?.ownerUsername;
        console.log(planetNames);

    useEffect(() => {
        if(!starSystem) loadStarSystem();
    }, [currentUser]);

    async function loadStarSystem()
    {
        setLoading(true);

        try {
            const header = currentUser ? { Authorization: `Bearer ${getToken()}` } : {},
                [data, planetData] = await Promise.all([
                    fetch(`/api/star-systems/${ID}`, { headers: header }),
                    fetch(`/api/planets?systemID=${ID}`, { headers: header })
                ]),
                [systemJSON, planetsJSON] = await Promise.all([
                    data.ok ? data.json() : null,
                    planetData.ok ? planetData.json() : null
                ]);

            setPlanets(planetsJSON);
            setStarSystem(systemJSON);
        } catch (err) {
            console.error(err);
            setStarSystem(null);
            setPlanets([]);
        } finally {
            setLoading(false);
        }
    }

    return (
        <main className="container mt-4">
            <div className="row justify-content-between">
                <Link to="/star-systems" className="btn btn-outline-light border-0 mb-3 col-2">
                    ← Back to Star Systems
                </Link>
                {canEdit &&
                    <button type="button" onClick={() => setEditVisible(true)} className="btn btn-outline-warning mb-3 col-2">
                        Edit System
                    </button>
                }
            </div>
            {!starSystem? <p className="text-danger">Something went wrong!</p> :
                loading? <p>Loading system...</p> :
                <article className="card bg-dark bg-opacity-75 text-light shadow">
                    <div className="card-body p-4">
                        <h1 className="card-title">{starSystem.name}</h1>
                        <div className="row g-3 align-items-center">
                            {[["Star Type:", starSystem.starType, ""],
                                ["Star Mass:", starSystem.starMass, "x the Sun"],
                                ["Distance from Earth:", starSystem.distanceFromEarth, " Ly"]]
                                .map(([stat, value, units]) =>
                                    <div key={stat} className="col-md-4">
                                        <b className="text-uppercase">{stat} </b>
                                        {value? value + units : "N/A"}
                                    </div>
                            )}
                            {!planetNames?.length? <i>No known planets</i> :
                                <div className="col-md-12">
                                    <b className="text-uppercase">Planets: </b> {planetNames.join(", ")}
                                </div>
                            }
                        </div>
                        <hr />
                        <details name="Description" className="lead">
                            <summary>Description</summary>
                            {starSystem.description}
                        </details>
                    </div>
                </article>    
            }
            {isEditVisible &&
                <SystemModal system={starSystem} planets={planetNames} token={getToken()}
                    onClose={() => setEditVisible(false)}
                    onSaved={updatedSystem => {
                        setStarSystem(updatedSystem);
                        setEditVisible(false);
                    }}
                />
            }
        </main>
    );
}