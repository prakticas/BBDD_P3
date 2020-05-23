/**3 compañias aereas mas puntuales*/
select aerolinea.nombre,t2.rnk
from (select aerolinea, RANK()over(order by por ASC) as rnk
    from (select count(tipo)/ count(*) as por,  vuelo.aerolinea  as aerolinea
        from (select vuelo,tipo
                from incidencia
                 where tipo='retrasado'
            ) inc
        right join 
        vuelo 
        on vuelo.idv= inc.vuelo
        group by vuelo.aerolinea
        ) t1
    )t2
inner join aerolinea
on aerolinea.id=t2.aerolinea
where t2.rnk <=3
;
/**2**/


/**3**/

select r.org as aeropuerto, t.aerolinea as aerolinea
from
(select org 
from
    (select org, RANK()over(order by por DESC) as rnk
    from 
        (select  tot.origen as org, cancelados/totales as por
        from
        (select vuelo.origen as org, count(*) as cancelados
        from
            vuelo 
        inner join
                (select * from incidencia where tipo='cancelado') inc
        on vuelo.idv= inc.vuelo
        group by vuelo.origen) can
        inner join 
        (select origen ,count(*) as totales 
        from vuelo 
        group by origen) tot
        on tot.origen=can.org)
    )
where rnk<=10
) r
inner join 
(
select distinct * from (/** contamos una única vez un aeropuerto por compañia**/
select aerolinea, origen as aero from vuelo
UNION 
select aerolinea, destino as aero from vuelo)
) t
on t.aero = r.org
;





