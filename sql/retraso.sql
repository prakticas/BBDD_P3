


select incidencia.id, auxiliar.carrierDelay
from incidencia 
inner join 
auxiliar
ON incidencia.vuelo=auxiliar.id
where incidencia.tipo='retrasado';