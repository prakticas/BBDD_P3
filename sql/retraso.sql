


select incidecia.id, auxiliar.carrierDelay
from incidecia 
inner join 
auxiliar
ON incidecia.vuelo=auxiliar.idv
where incidecia.tipo="retraso";