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
SELECT Estado, PORCENTAJE, rnk 
FROM (SELECT Estado, PORCENTAJE, RANK() over (order by PORCENTAJE DESC) as rnk
        FROM (SELECT jdesv.Estado, jdesv.desv*100/jtot.tot as PORCENTAJE 
            FROM (SELECT Estado,COUNT(*) as desv FROM AEROPUERTO join desvio ON Aeropuerto.id=desvio.newaeropuerto GROUP BY Estado) jdesv
            JOIN
            (SELECT Estado,COUNT(*) as tot FROM AEROPUERTO join vuelo ON Aeropuerto.id=vuelo.origen GROUP BY Estado) jtot
            ON  jdesv.Estado=jtot.estado)) where rnk <=3;
/**3**/




