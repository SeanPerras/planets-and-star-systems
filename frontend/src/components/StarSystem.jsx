export default function StarSystem({name, starType, starMass, distanceFromEarth, planetNames})
{
    return (
        <div className="card mb-2 mx-2 shadow-sm">
            <h4 className="card-header bg-dark text-white mb-0">{name}</h4>
            <div className="card-body bg-secondary-subtle">
                <div className="row g-3 align-items-center">
                    {[["Star Type:", starType, ""],
                        ["Star Mass:", starMass, "x the Sun"],
                        ["Distance from Earth:", distanceFromEarth, " Ly"]]
                        .map(([stat, value, units]) =>
                            <div key={stat} className="col-md-4">
                                <b className="text-uppercase">{stat} </b>
                                {value? value + units : "N/A"}
                            </div>
                    )}
                    {!planetNames?.length ? <i>No known planets</i> :
                        <div className="col-md-12">
                            <b className="text-uppercase">Planets: </b> {planetNames.join(", ")}
                        </div>
                    }
                </div>
            </div>
        </div>
    );
}