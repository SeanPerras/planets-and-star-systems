import React, { useEffect, useState } from "react";
import StarSystem from "../components/StarSystem";
import { Link } from "react-router-dom";
import SystemModal from "../components/modals/SystemModal";


export default function StarSystems({ currentUser, getToken })
{
    const [starSystems, setStarSystems] = useState([]),
        [planets, setPlanets] = useState([]),
        [loading, setLoading] = useState(false),
        [searched, setSearched] = useState(false);

    const [isCreateVisible, setCreateVisible] = useState(false);

    useEffect(() => {
        loadPage();
    }, [currentUser]);

    async function loadPage()
    {
        setLoading(true);
        try {
            const header = currentUser ? { Authorization: `Bearer ${getToken()}` } : {},
                [systemData, planetsData] = await Promise.all([
                    fetch("/api/star-systems", { headers: header }),
                    fetch("/api/planets", { headers: header })
                ]),
                [systemJSON, planetsJSON] = await Promise.all([
                    systemData.ok? systemData.json() : null,
                    planetsData.ok? planetsData.json() : null
                ]);

            setPlanets(planetsJSON);
            setStarSystems(systemJSON);
        } catch (err) {
            //console.error(err);
            setPlanets(null);
            setStarSystems(null);
        } finally {
            setLoading(false);
        }
    }


    async function searchStarSystems(e)
    {
        if (e) e.preventDefault();

        const formData = new FormData(e.target),
            searchParameters = new URLSearchParams([...formData.entries()]
                .filter(([k, v]) => v.trim() !== "")).toString(),
            parameters = searchParameters? `?${searchParameters}` : "";

        setLoading(true);

        try {
            const url = "/api/star-systems" + parameters,
                header = currentUser ? { Authorization: `Bearer ${getToken()}` } : {},
                data = await fetch(url, { headers: header }),
                starSystemJSON = data.ok ? await data.json() : null;
            
            setStarSystems(starSystemJSON);
        } catch (err) {
            setStarSystems(null);
        } finally {
            setLoading(false);
            setSearched(Boolean(e));
        }
    }

    const getPlanetNames = (ID) =>
        planets?.filter(p => p.systemID === ID)
            .map(p => p.name) ?? [];

    return (
        <div className="container mt-4">
            <form onSubmit={searchStarSystems} className="p-3 mb-4 border rounded">
                <div className="d-flex gap-2 align-items-stretch">
                    <div className="w-75">
                        <div className="d-flex gap-2">
                            <div className="w-50 rounded bg-secondary bg-opacity-50 p-1">
                                <label htmlFor="name" className="form-label text-white">System Name:</label>
                                <input type="text" name="name" id="name" className="shadow-sm form-control" />
                            </div>
                            <div className="w-50 rounded bg-secondary bg-opacity-50 p-1">
                                <label htmlFor="starType" className="form-label text-white">Star Type:</label>
                                <input type="text" name="starType" id="starType" className="shadow-sm form-control" />
                            </div>
                            <div className="w-25 rounded bg-secondary bg-opacity-50 p-1">
                                <label htmlFor="planetCount" className="form-label text-white"> Planet Count:</label>
                                <input id="planetCount" name="planetCount" type="number" min="1" step="any"
                                    className="shadow-sm form-control" />
                            </div>
                        </div>
                        <div className="d-flex gap-2 mt-2">
                            <div className="w-75 rounded bg-secondary bg-opacity-50 p-1">
                                <select id="systemSortBy" name="sortBy" className="form-select">
                                    <option value="">Sort By</option>
                                    <option value="name">Name</option>
                                    <option value="starType">Star Type</option>
                                </select>
                            </div>
                            <div className="w-25 rounded bg-secondary bg-opacity-50 p-1 align-self-center">
                                <div className="d-inline-flex align-items-center gap-2">
                                    <label htmlFor="systemSortDirection" className="form-check-label text-white">
                                        Descending:
                                    </label>
                                    <input type="checkbox" id="systemSortDirection" name="descending" defaultChecked={false}
                                        className="form-check-input m-0"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="w-25">
                        <div className="h-100 d-flex flex-column justify-content-center">
                            <button type="submit" className="w-100 align-self-center btn btn-outline-secondary shadow-sm" disabled={loading}>
                                {!loading ? <b>Search</b> : <i className="text-muted">Searching...</i>}
                            </button>
                            <button type="button" onClick={() => setCreateVisible(true)} className="w-100 mt-1 btn btn-outline-success">
                                Create Your Own Star System!
                            </button>
                        </div>
                    </div>
                </div>
                {searched && (starSystems?.length?
                            <span className="fw-semibold text-white">{`${starSystems.length} Star System${starSystems.length === 1 ? "" : "s"} Found`}</span> :
                            <span className="text-warning"><i>No star systems found</i></span>
                )}
            </form>
            {loading? <h2>Loading Star Systems...</h2> :
                !starSystems? <figure className="text-danger">Whoops, something went wrong!</figure> :
                    starSystems.map(s => 
                        <div key={s.id + s.name} className="col">
                            <Link to={`/star-systems/${s.id}`} state={{ starSystem:s, planets }} className="text-decoration-none">
                                <StarSystem {...s} planetNames={getPlanetNames(s.id)} />
                            </Link>
                        </div>
                    )
            }
            {isCreateVisible &&
                <SystemModal
                    onClose={() => setCreateVisible(false)}
                    onSaved={createdSystem => {
                        setStarSystems(prev => [...prev, createdSystem]);
                        setCreateVisible(false);
                    }}
                    planets={planets}
                    token={getToken()}/>
            }
        </div>
    );
}