
CREATE TABLE DESVIO(
ID number(11) PRIMARY KEY,
FOREIGN KEY(ID) REFERENCES INCIDENCIA(ID) ON DELETE CASCADE,
NEWAVION REFERENCES VUELO(IDV) ON DELETE CASCADE,
NEWAEREOPUERTO REFERENCES VUELO(IDV) ON DELETE CASCADE
)

select incidecia.id, auxiliar.div1airport,auxiliar.div1TailNum
from incidecia 
inner join 
auxiliar
ON incidecia.vuelo=auxiliar.idv
where incidecia.tipo="desviado1";

select incidecia.id, auxiliar.div2airport,auxiliar.div2TailNum
from incidecia 
inner join 
auxiliar
ON incidecia.vuelo=auxiliar.idv
where incidecia.tipo="desviado2";