/**1*/
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
/**En esta version de la consulta 2 hacemos el porcentaje**/
SELECT Estado, PORCENTAJE, rnk 
FROM (SELECT Estado, PORCENTAJE, RANK() over (order by PORCENTAJE DESC) as rnk
        FROM (SELECT jdesv.Estado, jdesv.desv*100/jtot.tot as PORCENTAJE 
            FROM (SELECT Estado,COUNT(*) as desv FROM AEROPUERTO join desvio ON Aeropuerto.id=desvio.newaeropuerto GROUP BY Estado) jdesv,
            (SELECT COUNT(*) as tot FROM vuelo) jtot)) where rnk <=3;

/**En esta version de la consulta 2 no hacemos el porcentaje**/
select * from(
    SELECT Estado, rnk 
FROM 
(   SELECT Estado,  RANK() over (order by desv DESC) as rnk
    FROM (
            SELECT Estado,COUNT(*) as desv 
            FROM AEROPUERTO 
            join desvio 
            ON Aeropuerto.id=desvio.newaeropuerto GROUP BY Estado) jdesv
)
)
where rnk<=3
;
/**3**/
SELECT aerolinea
from 
(select aerolinea,RANK()over (order by num DESC) as rnk
from 
(select aerolinea.nombre as aerolinea, count(*) as num
from
(select org 
from
    (select org, RANK()over(order by cancelados DESC) as rnk
    from 
            ( /**cancelados por aereopuerto**/
            select vuelo.origen as org, count(*) as cancelados
            from vuelo 
            inner join
            (select * from incidencia where tipo='cancelado') inc
            on vuelo.idv= inc.vuelo
            group by vuelo.origen
            ) can
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
inner join 
aerolinea 
on aerolinea.id = t.aerolinea
GROUP BY  aerolinea.nombre))
where rnk =1
;



/**Consulta no trivial 1: media retraso 5 estados con menos vuelos**/
SELECT AEROPUERTO.ESTADO, AVG(VI.Tiempo) FROM 
(SELECT Vuelo.origen, RT.tiempo
    FROM (SELECT incidencia.vuelo, retraso.tiempo from Retraso JOIN Incidencia ON Incidencia.id=Retraso.id) RT 
        JOIN VUELO 
            ON Rt.vuelo=Vuelo.idv) VI
    JOIN AEROPUERTO ON AEROPUERTO.id=VI.origen 
          JOIN (SELECT ESTADO
                FROM (SELECT ESTADO, RANK() over (order by vuelosXest ASC) as rnk
                    from (SELECT ESTADO, COUNT(*) as vuelosXest 
                          FROM AEROPUERTO join vuelo 
                            ON AEROPUERTO.id=vuelo.origen Group by estado)) where rnk <= 5) mv 
          ON AEROPUERTO.ESTADO=mv.ESTADO group by AEROPUERTO.Estado;


/*version con tabla join precalculado**/
          SELECT AEROPUERTO.ESTADO, AVG(VI.Tiempo) FROM 
(SELECT Vuelo.origen, RT.tiempo
    FROM (SELECT vuelo, tiempo from INCDESV) RT 
        JOIN VUELO 
            ON Rt.vuelo=Vuelo.idv) VI
    JOIN AEROPUERTO ON AEROPUERTO.id=VI.origen 
          JOIN (SELECT ESTADO
                FROM (SELECT ESTADO, RANK() over (order by vuelosXest ASC) as rnk
                    from (SELECT ESTADO, COUNT(*) as vuelosXest 
                          FROM AEROPUERTO join vuelo 
                            ON AEROPUERTO.id=vuelo.origen Group by estado)) where rnk <= 5) mv 
          ON AEROPUERTO.ESTADO=mv.ESTADO group by AEROPUERTO.Estado;



/**Consulta no trivial 2: mayor tipo de incidencia por tipo de avion **/

select modelo, tipo, num 
from
(select modelo, tipo, num, RANK() over (partition by modelo order by num DESC) as rnk from
        (select avuel.modelo as modelo, tipo, count(tipo) as num
        from (select vuelo,tipo
                from incidencia
            ) inc
        join 
        (select idv, modelo 
        from avion 
        join 
        vuelo 
        on avion.matricula=vuelo.transporte) avuel 
        on avuel.idv= inc.vuelo
        group by tipo, avuel.modelo)
)
where rnk=1;

/**Consulta no trivial 3: destinos donde mas se va por aerolinea sin contar vuelos cancelados **/
select aerolinea.nombre as aerolinea, aeropuerto.nombre as aereopuerto
from 
(   select aerolinea,destino, RANK() over (partition by aerolinea order by  count(destino)  DESC) as rnk
    from(
        select vuelo.idv,aerolinea,destino  /**vuelos sin cancelaciones**/
        from vuelo
        minus
        select  vuelo.idv,aerolinea, destino
        from vuelo
        inner join 
        incidencia
        on vuelo.idv = incidencia.vuelo
        where tipo='cancelado'
    )
    group by aerolinea,destino
    order by aerolinea,rnk
) t
inner join aerolinea 
on aerolinea.id = t.aerolinea
inner join aeropuerto
on aeropuerto.id = t.destino
where rnk =1
;





