

PARTITION BY LIST (tipo)
(PARTITION q1_cancelado ('cancelado')
PARTITION q2_retrasado ('retrasado')
PARTITION q3_desviado ('desviado')
);

CREATE INDEX desv_aer ON Desvio(newaeropuerto);

EXPLAIN PLAN FOR select aerolinea.nombre,t2.rnk
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

SELECT PLAN_TABLE_OUTPUT FROM
TABLE(DBMS_XPLAN.DISPLAY());

EXPLAIN PLAN FOR select * from(
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
      
SELECT PLAN_TABLE_OUTPUT FROM
TABLE(DBMS_XPLAN.DISPLAY());

EXPLAIN PLAN FOR select distinct aerolinea.nombre as aerolinea
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
;
      
SELECT PLAN_TABLE_OUTPUT FROM
TABLE(DBMS_XPLAN.DISPLAY());
