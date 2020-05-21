
CREATE TABLE DESVIO(
ID number(11) PRIMARY KEY,
FOREIGN KEY(ID) REFERENCES INCIDENCIA(ID) ON DELETE CASCADE,
NEWAVION REFERENCES avion(matricula) ON DELETE CASCADE,
NEWAEREOPUERTO REFERENCES aeropuerto(ID) ON DELETE CASCADE
)

insert into DESVIO(ID,NEWAEREOPUERTO,NEWAVION)
select incidencia.id, auxiliar.DIV1AIRPORT,auxiliar.div1TailNum
from incidencia 
inner join 
auxiliar
ON incidencia.vuelo=auxiliar.id
where incidencia.tipo='desviado1';

insert into DESVIO(ID,NEWAEREOPUERTO,NEWAVION)
select incidencia.id, auxiliar.div2airport,auxiliar.div2TailNum
from incidencia 
inner join 
auxiliar
ON incidencia.vuelo=auxiliar.id
where incidencia.tipo='desviado2';