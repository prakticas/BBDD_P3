
CREATE TABLE DESVIO(


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