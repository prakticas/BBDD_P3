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




