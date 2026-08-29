import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Planet from "../components/Planet";
import PlanetModal from "../components/modals/PlanetModal";

export default function Planets({currentUser, getToken})
{
    const [planets, setPlanets] = useState([]),
        [systemDesigs, setSystemDesigs] = useState([]),
        [loading, setLoading] = useState(false);

    const systemNames = Object.fromEntries(
        (systemDesigs || []).map(system => [system.ID, system.name])
    );
        
    const [systemID, setSystemID] = useState(""),
        [isRogueEnabled, setRogueEnabled] = useState(null),
        [searched, setSearched] = useState(false);

    const [isCreateVisible, setCreateVisible] = useState(false);
    
    useEffect(() => {
        loadPage();
    }, [currentUser]);

    async function loadPage()
    {
        setLoading(true);
        try {
            const header = currentUser? { Authorization: `Bearer ${getToken()}`} : {},
                [planetData, systemData] = await Promise.all([
                    fetch("/api/planets", { headers: header }),
                    fetch("/api/star-systems/designators", { headers: header })
                ]),
                [planetJSON, systemJSON] = await Promise.all([
                    planetData.ok ? planetData.json() : null,
                    systemData.ok ? systemData.json() : null
                ]);

            setPlanets(planetJSON);
            setSystemDesigs(systemJSON);
        } catch (err) {
            setPlanets(null);
            setSystemDesigs(null);
        } finally {
            setLoading(false);
        }
    }

    async function searchPlanets(e)
    {
        if(e) e.preventDefault();

        const formData = new FormData(e.target),
            searchParameters = new URLSearchParams([...formData.entries()]
                .filter(([k, v]) => v.trim() !== "")).toString(),
            parameters = searchParameters? `?${searchParameters}` : "";

        setLoading(true);
        try {
            const url = "/api/planets" + parameters,
                header = currentUser ? { Authorization: `Bearer ${getToken()}` } : {},
                data = await fetch(url, { headers: header }),
                json = data.ok ? await data.json() : null;
            setPlanets(json);
        } catch (err) {
            setPlanets(null);
        } finally {
            setLoading(false);
            setSearched(true);
        }
    }
    
    return (
        <div className="container mt-4">
            <form onSubmit={searchPlanets} className="p-3 mb-4 border rounded">
                <div className="d-flex gap-2 align-items-stretch">
                    <div className="w-75">
                        <div className="d-flex gap-2">
                            <div className="w-25 rounded bg-secondary bg-opacity-50 p-1">
                                <label htmlFor="systemID" className="form-label text-white">System:</label>
                                <select name="systemID" id="systemID" value={systemID}
                                    onChange={(e) => setSystemID(e.target.value)}
                                    className="form-select">
                                    <option value="">All Systems</option>
                                    {systemDesigs?.map(sys =>
                                        <option key={sys.ID + sys.name} value={sys.ID}>{sys.name}</option>
                                    )}
                                </select>
                            </div>
                            <div className="w-50 rounded bg-secondary bg-opacity-50 p-1">
                                <label htmlFor="name" className="form-label text-white">Planet Name:</label>
                                <input type="text" name="name" id="name" className="shadow-sm form-control" />
                            </div>
                            <div className="w-25 rounded bg-secondary bg-opacity-50 p-1">
                                <label className={`form-label ${systemID === "" ? "text-white" : "text-muted"}`}>
                                    Rogue Planets:
                                </label>
                                <div className="d-flex flex-nowrap mt-1">
                                    {["Only", "All", "None"].map(b =>
                                        <div key={b} className="form-check form-check-inline">
                                            <input type="radio" id={"rogue" + b} name="rogue"
                                                disabled={systemID !== ""}
                                                value={b !== "All" ? (b === "Only") : ""}
                                                checked={isRogueEnabled === (b !== "All" ? (b === "Only") : null)}
                                                onChange={(e) => setRogueEnabled(e.target.value !== "" ? (e.target.value === "true") : null)}
                                                className="form-check-input" />
                                            <label htmlFor={"rogue" + b} className={`form-check-label ${systemID === "" ? "text-white" : "text-muted"}`}>
                                                {b}
                                            </label>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                        <div className="d-flex gap-2 mt-2">
                            <div className="w-75 rounded bg-secondary bg-opacity-50 p-1">
                                <select id="planetSortBy" name="sortBy" className="form-select">
                                    <option value="">Sort By</option>
                                    <option value="name">Name</option>
                                    <option value="mass">Mass</option>
                                </select>
                            </div>
                            <div className="w-25 rounded bg-secondary bg-opacity-50 p-1 align-self-center">
                                <div className="d-inline-flex align-items-center gap-2">
                                    <label htmlFor="planetSortDirection" className="form-check-label text-white">
                                        Descending:
                                    </label>
                                    <input type="checkbox" id="planetSortDirection" name="descending" defaultChecked={false}
                                        className="form-check-input m-0"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="w-25">
                        <div className="h-100 d-flex flex-column justify-content-center">
                            <button type="submit" className="w-100 align-self-center btn btn-outline-secondary shadow-sm" disabled={loading} >
                                {!loading ? <b>Search</b> : <i className="text-muted">Searching...</i>}
                            </button>
                            <button type="button" onClick={() => setCreateVisible(true)} className="w-100 mt-1 btn btn-outline-success">
                                Create Your Own Planet!
                            </button>
                        </div>
                    </div>
                </div>
                {searched && (planets?.length ?
                    <span className="fw-semibold text-white">{`${planets.length} Planet${planets.length == 1 ? "" : "s"} Found`}</span> :
                    <span className="text-warning"><i>No planets spotted</i></span>)}
            </form>
            {loading ? <h2>Loading Planets...</h2> :
                !planets ? <figure className="text-danger">Whoops, something went wrong!</figure> :
                    <div className="row row-cols-md-2 g-3 mb-5">
                        {planets.map(p =>
                            <div key={p.id + p.name} className="col">
                                <Link to={`/planets/${p.id}`} state={{planet:p, systemDesigs}} className="text-decoration-none">
                                    <Planet {...p} systemName={systemNames[p.systemID]} />
                                </Link>
                            </div>
                        )}
                    </div>
            }
            {isCreateVisible &&
                <PlanetModal
                    onClose={() => setCreateVisible(false)}
                    onSaved={createdPlanet => {
                        setPlanets(prev => [...prev, createdPlanet]);
                        setCreateVisible(false);
                    }}
                    systemDesigs={systemDesigs}
                    token={getToken()}/>
            }
            {/* {isCreateVisible && <div className="modal-backdrop show" />} */}
        </div>
    );
};