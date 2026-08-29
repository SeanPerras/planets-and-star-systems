import { useState } from "react";

const NASALinks = [
    "https://science.nasa.gov/solar-system/planets/",
    "https://science.nasa.gov/exoplanets/exoplanet-catalog/",
    "https://science.nasa.gov/universe/what-happens-when-something-gets-too-close-to-a-black-hole/"
],
    CSSLink = "https://projects.verou.me/css3patterns/#starry-night";

export default function Home()
{
    return (
        <div id="home" className="container mt-4">
            <h1 className="fw-bold text-white-50">Planets and Star Systems</h1>
            <p className="fs-5 text-white-50">Space is my favorite thing of all the things. No matter what I've done in my life, I come back to space.<br/>
                Scientifically, creatively, and for a short time, spiritually. If I fell into a black hole, I'd be the happiest <a href={NASALinks[2]} target="_blank" rel="noopener noreferrer">spaghettified</a> human you'd ever seen.<br/>
                So, when it came time to start this project, I immediately went to space first.<br/>
                I liked the idea of implementing planets as individual components that have custom information to make it more interactive.<br/>
                Planets, naturally, group together into Star Systems, or maybe not; perhaps it's a Rogue Planet!<br/>
                I hope you enjoy this page and have fun creating your own astronomical bodies!</p>
            <footer className="fw-bold text-white-50 mt-5"><u>Further information on <a href={NASALinks[0]} target="_blank" rel="noopener noreferrer">our local</a> planets!</u></footer>
            <footer className="fw-bold text-white-50"><u>How about a similar, but more complete and federally subsidized resource for <a href={NASALinks[1]} target="_blank" rel="noopener noreferrer">real exoplanets!</a></u></footer>
            <footer className="fw-bold text-white-50"><u>Like the background? Check out the inspiration for it <a href={CSSLink} target="_blank" rel="noopener noreferrer">here</a>!</u></footer>
        </div>
    )
}