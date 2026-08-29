create database if not exists flexpath_final;
use flexpath_final;

drop table if exists planets, star_systems, roles, users;
create table users (
    username varchar(255) primary key,
    password varchar(255)
);
create table roles (
    username varchar(255) not null,
    role varchar(250) not null,
    primary key (username, role),
    foreign key (username) references users(username) on delete cascade
);

insert into users (username, password) values ('admin', '$2a$10$tBTfzHzjmQVKza3VSa5lsOX6/iL93xPVLlLXYg2FhT6a.jb1o6VDq');
insert into roles (username, role) values ('admin', 'ADMIN');
insert into users (username, password) values ('user', '$2a$10$tBTfzHzjmQVKza3VSa5lsOX6/iL93xPVLlLXYg2FhT6a.jb1o6VDq');
insert into roles (username, role) values ('user', 'USER');

create table if not exists star_systems (
    id int primary key auto_increment,
    name varchar(255) not null,
    star_type varchar(100),
    star_mass decimal(10,3),
    distance_from_Earth decimal(12,3),
    description text,
    is_public bool not null default FALSE,
    owner_username varchar(255) not null,
    
    foreign key (owner_username) references users(username) on delete cascade
);
create table if not exists planets (
    id int primary key auto_increment,
    name varchar(100) not null,
    type varchar(100),
    mass decimal(10,3),
    distance_from_star decimal(12,3),
    description text,
    moons json,
    is_public bool not null default FALSE,
    owner_username varchar(255) not null,
    system_id int default null,
    
    foreign key (owner_username) references users(username) on delete cascade,
    foreign key (system_id) references star_systems(id) on delete set null
);

insert into users (username, password) values ('Sean', '$2a$10$A.XMZ8h7bJtVGn2nLoZEBOXxHUUibGbAAirZu/oIICar6X.84.mYW');
insert into roles (username, role) values ('Sean', 'USER');

insert into star_systems (name, star_type, star_mass, distance_from_Earth, description, is_public, owner_username)
values ('Our Solar System', 'Yellow Main-Sequence', 1, NULL, 'Home sweet home.', TRUE, 'admin');
INSERT INTO star_systems (name, star_type, star_mass, distance_from_Earth, description, is_public, owner_username)
VALUES ('Kepler-452', 'Yellow Main-Sequence', 1.04, 1810, 'A Sun-like star in the constellation Cygnus. It is slightly more massive and brighter than the Sun and is known to host a confirmed exoplanet, a super-Earth that orbits near the system\'s habitable zone.',
		TRUE, 'admin');
INSERT INTO star_systems (name, star_type, star_mass, distance_from_Earth, description, is_public, owner_username)
VALUES ('Alpha Centauri', 'Trinary: Yellow, Orange & a Red Main-Sequence', 2.17, 4.34, 'Alpha Centauri is the closest stellar system to Earth. Alpha Centauri A is the Yellow star similar to the Sun 1.1x its mass, Alpha Centauri B is a somewhat smaller orange star at .9x the Sun, and Proxima Centauri, also known as Alpha Centauri C, is a small red dwarf at .12x the Sun and orbits much farther away. The only confirmed planets in the system orbit Proxima Centauri/C; there are none confirmed around either A or B.',
        TRUE, 'admin');

insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Mercury', 'Terrestrial', 0.055, 0.39, 'The first planet. Its heavily cratered surface experiences dramatic temperature changes because it has almost no atmosphere to retain heat',
		'[]', TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Venus', 'Terrestrial', 0.815, 0.72, 'Our warm sibling. Venus is Earth-sized but dramatically less hospitable, with a thick carbon-dioxide atmosphere that produces an extreme runaway greenhouse effect. Its surface is hotter than Mercury despite being farther from the Sun.',
		'[]', TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Earth', 'Terrestrial', 1.000, 1.000, 'The Pale Blue Dot. The only world currently known to support life. Its liquid-water oceans, protective atmosphere, magnetic field, and single large moon all contribute to a remarkably stable and life-friendly environment.',
		'["The Moon"]', TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Mars', 'Terrestrial', 0.107, 1.52, 'Our weird sibling. A cold, dusty world known for its red appearance, giant volcanoes, deep canyons, and polar ice caps. Evidence shows that liquid water once flowed across parts of its surface, making it a major target in the search for past microbial life.',
		'["Phobos", "Deimos"]',
        TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Jupiter', 'Gas Giant', 317.8, 5.2, 'The big one! The largest planet made mostly of hydrogen and helium. Its Great Red Spot is a long-lived storm larger than Earth.',
		'["Io", "Europa", "Ganymede", "Callisto", "111 more!"]',
        TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Saturn', 'Gas Giant', 95.2, 9.54, 'The ring-bearer. Famous for its vast and bright ring system, which is made largely of ice particles and rocky debris.',
		'["Mimas", "Enceladus", "Tethys", "Dione", "Rhea", "Titan", "Hyperion", "Iapetus", "285 more!"]',
        TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Uranus', 'Ice Giant', 14.5, 19.2, 'The butt of all the jokes. With a blue-green color produced by methane in its atmosphere, it is mostly unusual because it rotates on its side, likely due to a massive collision early in the Solar System\'s history, creating extreme seasonal conditions.',
		'["Miranda", "Ariel", "Umbriel", "Titania", "Oberon", "24 more!"]',
        TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Neptune', 'Ice Giant', 17.1, 30.06, 'The other blue guy. With a strikingly deep blue atmosphere, it has some of the fastest winds measured anywhere in the Solar System, and its largest moon Triton is thought to be a captured Kuiper Belt object.',
		'["Naiad", "Thalassa", "Despina", "Galatea", "Larissa", "Proteus", "Triton", "Nereid", "9 more!"]',
        TRUE, 'admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Pluto', 'Dwarf', 0.002, 39.5, 'The little guy. So far out that it\' actually in the Kuiper Belt, a distant region filled with icy bodies beyond Neptune. Although it was reclassified as a dwarf planet in 2006, it remains a complex world with mountains, glaciers, a thin atmosphere, and five known moons.',
		'["Charon", "Styx", "Nix", "Kerberos", "Hydra"]',
        TRUE, 'admin', 1);
        
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Kepler-452b', 'Super-Earth', 3.29, 1.046, 'Kepler-452b is a confirmed super-Earth discovered by the Kepler mission in 2015. It has an orbital period of about 384.8 days. Its orbit lies in the stars habitable zone, meaning liquid water could potentially exist under suitable atmospheric conditions, but its surface, atmosphere, and actual habitability remain unknown.',
		'[]', TRUE, 'admin', 2);
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Proxima Centauri b', 'Super-Earth', 1.07, 0.049, 'Proxima Centauri b is a super-Earth orbiting within the habitable zone of Proxima Centauri. It completes an orbit in only about 11.2 days because it circles extremely close to its small, cool host star. Its proximity makes it one of the most compelling nearby exoplanets, though powerful stellar flares may make its environment hostile to life as we know it.',
		'[]', TRUE, 'admin', 3);
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Proxima Centauri d', 'Terrestrial', 0.26, 0.029, 'Proxima Centauri d is a very small terrestrial exoplanet orbiting much closer to Proxima Centauri than Proxima b. With an orbital period of roughly 5.1 days, it is likely exposed to intense radiation and is well inside the stars habitable zone.',
		'[]', TRUE, 'admin', 3);
        
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
VALUES ('Test', 'Test World', 1.25, NULL, 'Created to test roles, authorities and endpoints.',
		'["Test Moon One", "Test Moon Two"]',
        FALSE, 'user', NULL);
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
VALUES ('Mr. Lonely', 'Ice Planet', 0.75, NULL, 'Just a random planet floating around the cosmos; no star to keep it warm.',
        '[]', TRUE, 'Sean', NULL);
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
VALUES ('Private Curl Test', 'Terrestrial', 1, NULL, 'Created during ownership testing.',
        '[]', FALSE, 'admin', NULL);