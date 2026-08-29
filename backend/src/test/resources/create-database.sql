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

insert into users (username, password) values ('test-admin', 'admin');
insert into roles (username, role) values ('test-admin', 'ADMIN');
insert into users (username, password) values ('test-user', 'user');
insert into roles (username, role) values ('test-user', 'USER');


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

insert into star_systems (name, star_type, star_mass, distance_from_Earth, description, is_public, owner_username)
values ('Our Solar System', 'Yellow Main-Sequence', 1, NULL, 'Home sweet home.', TRUE, 'test-admin');
INSERT INTO star_systems (name, star_type, star_mass, distance_from_Earth, description, is_public, owner_username)
VALUES ('Kepler-452', 'Yellow Main-Sequence', 1.04, 1810, 'A sun-like star with a potentially habitable world.',
		FALSE, 'test-user');
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Mercury', 'Terrestrial', 0.055, 0.39, 'The first planet.', '[]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Venus', 'Terrestrial', 0.815, 0.72, 'Our warm sibling.', '[]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Earth', 'Terrestrial', 1.000, 1.000, 'The Pale Blue Dot.', '["The Moon"]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Mars', 'Terrestrial', 0.107, 1.52, 'Our weird sibling.', '["Phobos", "Deimos"]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Jupiter', 'Gas Giant', 317.8, 5.2, 'The big one!', '["Ganymede", "95 more!"]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Saturn', 'Gas Giant', 95.2, 9.54, 'The ring-bearer.', '["Titan", "146 more!"]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Uranus', 'Ice Giant', 14.5, 19.2, 'The butt of all the jokes.', '["Titania", "27 more!"]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Neptune', 'Ice Giant', 17.1, 30.06, 'The other blue guy.', '["Triton", "14 more!"]',
		TRUE, 'test-admin', 1);
insert into planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
values ('Pluto', 'Dwarf', 0.002, 39.5, 'The little guy.', '["Charon", "Styx", "Nix", "Kerberos", "Hydra"]',
		TRUE, 'test-admin', 1);
INSERT INTO planets (name, type, mass, distance_from_star, description, moons, is_public, owner_username, system_id)
VALUES ('Shadow Planet', 'Unknown', 0.5, null, 'A hidden world.', '[]', FALSE, 'test-user', NULL); 