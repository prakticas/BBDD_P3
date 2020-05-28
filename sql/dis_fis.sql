

PARTITION BY LIST (tipo)
(PARTITION q1_cancelado ('cancelado')
PARTITION q2_retrasado ('retrasado')
PARTITION q3_desviado ('desviado')
);

CREATE INDEX desv_aer ON Desvio(newaeropuerto);
CREATE INDEX inc_vuel ON incidencia(vuelo);

CREATE TABLE INCRET (
	Id Number(11),
	VUELO REFERENCES VUELO(IDV) ON DELETE CASCADE,
    Tiempo Number(11),
    PRIMARY KEY (Id),
	Foreign key(Id) references incidencia(id) ON DELETE CASCADE
);

INSERT INTO INCRET(ID,VUELO,Tiempo)
select id,vuelo, Tiempo
from 
incidencia
natural join 
retraso;

CREATE TABLE INCDESV (
	Id Number(11),
	VUELO REFERENCES VUELO(IDV) ON DELETE CASCADE,
   	PRIMARY KEY (Id),
	Foreign key(Id) references incidencia(id) ON DELETE CASCADE,
	NEWAVION REFERENCES avion(matricula) ON DELETE CASCADE,
	NEWAEROPUERTO REFERENCES aeropuerto(ID) ON DELETE CASCADE
);

INSERT INTO INCDESV(ID,VUELO,NEWAVION,NEWAEROPUERTO)
select id,vuelo, NEWAVION,NEWAEREOPUERTO
from 
incidencia
natural join 
Desvio;
