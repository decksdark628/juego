DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS partidas CASCADE;
DROP TABLE IF EXISTS comentarios CASCADE;
CREATE TABLE usuarios(
    alias VARCHAR(255) NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    nivel INTEGER NOT NULL
);
ALTER TABLE
    usuarios ADD PRIMARY KEY(alias);

CREATE TABLE partidas(
    id SERIAL NOT NULL,
    fecha_hora TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    puntaje INTEGER NOT NULL,
    alias_usuario VARCHAR(255) NOT NULL 
);
ALTER TABLE
    partidas ADD PRIMARY KEY(id);
ALTER TABLE
    partidas ADD CONSTRAINT partidas_fecha_hora_unique UNIQUE(fecha_hora);

CREATE TABLE comentarios(
    id SERIAL NOT NULL,
    texto TEXT NOT NULL,
    id_partida INTEGER NOT NULL,
    alias_usuario VARCHAR(255) NOT NULL
);
ALTER TABLE
    comentarios ADD PRIMARY KEY(id);
    
ALTER TABLE
    comentarios ADD CONSTRAINT comentarios_alias_usuario_foreign FOREIGN KEY(alias_usuario) REFERENCES usuarios(alias);
ALTER TABLE
    partidas ADD CONSTRAINT partidas_alias_usuario_foreign FOREIGN KEY(alias_usuario) REFERENCES usuarios(alias);
ALTER TABLE
    comentarios ADD CONSTRAINT comentarios_id_partida_foreign FOREIGN KEY(id_partida) REFERENCES partidas(id);
