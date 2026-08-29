export default function Planet({ name, type, mass, distanceFromStar, moons, systemName })
{
    const moonsArr = moons ? JSON.parse(moons) : [];

    function formatMoons(mArray, maxChar = 42)
    {
        if (!mArray.length) return <i>No known moons.</i>;

        let list = "", count = 0, remaining,
            manualRemaining = null, lastLoop = mArray.length;

        const suffixReserve = `, many more!`.length,
            match = mArray[mArray.length - 1].match(/^(\d+) more!$/);

        if(match) {
            manualRemaining = parseInt(match[1]);
            lastLoop--;
        }

        for (let i = 0; i < lastLoop; i++) {
            const next = list? `${list}, ${mArray[i]}` : mArray[i];
            if((i + 1 === lastLoop && next.length > maxChar) ||
               (i + 1 !== lastLoop && (next.length + suffixReserve > maxChar)))
                break;

            list = next;
            count++;
        }

        const skipped = lastLoop - count;//if manual lastLoop == mArray length, else mA.len - 1
        if(manualRemaining)
            remaining = manualRemaining + skipped;//skipped == mA len - count; accounting for placeholder
        else remaining = skipped;//skipped == mA len - 1 - count; one less skipped -> the lack of placeholder

        const maxSuff = remaining < 999 ? `${remaining}` : "many";

        if(!count) return `${remaining} moon${remaining > 1 ? "s" : ""}!`;
        return remaining > 0 ? `${list}, ${maxSuff} more!` : list;
    }

    return (
        <div className="card shadow-sm bg-transparent">
            <h4 className="card-header text-white mb-0">{name} - <i className="fw-normal text-white-50">{systemName ?? "Rogue"}</i></h4>
            <div className="card-body bg-secondary-subtle rounded">
                <div className="row g-3 align-items-center">
                    {[["Type:", type, ""],
                        ["Mass:", mass, "x Earth"],
                        ["Distance:", distanceFromStar, " AU"]]
                        .map(([stat, value, units]) =>
                            <div key={stat} className="col-md-4">
                                <b className="text-uppercase">{stat} </b>
                                {value? value + units : "N/A"}
                            </div>
                    )}
                    <div className="col-md-12">
                        <b>Moons: </b>
                        {formatMoons(moonsArr)}
                    </div>
                </div>
            </div>
        </div>
    );
}