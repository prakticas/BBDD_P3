

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
	Foreign key(Id) references incidencia(id)
);

INSERT INTO INCRET(ID,VUELO,Tiempo)
select id,vuelo, Tiempo
from 
incidencia
natural join 
retraso;
