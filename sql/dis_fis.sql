

PARTITION BY LIST (tipo)
(PARTITION q1_cancelado ('cancelado')
PARTITION q2_retrasado ('retrasado')
PARTITION q3_desviado ('desviado')
);

CREATE INDEX desv_aer ON Desvio(newaeropuerto);