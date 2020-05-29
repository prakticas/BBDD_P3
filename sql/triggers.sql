
/**NO BORRAR DE DESVIO**/
CREATE or REPLACE TRIGGER NOBORRARDES
BEFORE DELETE ON DESVIO
BEGIN
  RAISE_APPLICATION_ERROR(-20000, 'No se pueden borrar Desvios, borre desde incidencia');
END NOBORRARDES;
/
/**NO BORRAR DE RETRASO**/
CREATE or REPLACE TRIGGER NOBORRARRET
BEFORE DELETE ON RETRASO
BEGIN
  RAISE_APPLICATION_ERROR(-20000, 'No se pueden borrar Retraso, borre desde incidencia');
END NOBORRARRET;
/


/**Mantenimiento tabla INCRET (es un join)**/
CREATE or REPLACE TRIGGER UPDINCRET
AFTER UPDATE ON RETRASO
FOR EACH ROW
BEGIN
  UPDATE INCRET SET Tiempo=:new.Tiempo WHERE ID=:old.ID;
END UPDINCRET;
/

/* no hace falta borrar de esta tabla, delete casacade 
 lo hara por nosotros*/

CREATE or REPLACE TRIGGER INSINCRET
AFTER INSERT ON RETRASO
FOR EACH ROW
BEGIN
  INSERT INTO INCRET(ID,VUELO,Tiempo)
  select id,vuelo, Tiempo
  from 
  incidencia
  natural join 
  retraso
  where id=:old.id ;
END INSINCRET;
/
/*solo se mira al incluir en retraso ya que obligatoriamnte existe
en incidencia (clave de retraso en delete cascade), por lo que existe esta id
en las dos tablas y se puede hacer join de dicha fila*/

/**solo hace falta borrar al deletar de retraso, ya que de incidencia esta en cascade*/


/**Mantenimiento tabla INCDESV (es un join)**/

/* no hace falta borrar de esta tabla, delete casacade 
 lo hara por nosotros*/
CREATE or REPLACE TRIGGER UPDINCDESV
AFTER UPDATE ON DESVIO
FOR EACH ROW
BEGIN
  UPDATE INCDESV SET NEWAVION=:new.NEWAVION, NEWAEROPUERTO=:new.NEWAEROPUERTO WHERE ID=:old.ID;
END UPDINCDESV;
/



CREATE or REPLACE TRIGGER INSINCDESV
AFTER INSERT ON DESVIO
FOR EACH ROW
BEGIN
INSERT INTO INCDESV(ID,VUELO,NEWAVION,NEWAEROPUERTO)
select id,vuelo, NEWAVION,NEWAEREOPUERTO
from 
incidencia
natural join 
Desvio
where id=:old.id ;
END INSINCDESV;
/
/*solo se mira al incluir en desvio ya que obligatoriamnte existe
en incidencia (clave de retraso en delete cascade), por lo que existe esta id
en las dos tablas y se puede hacer join de dicha fila*/

/**solo hace falta borrar al deletar de desvio, ya que de incidencia esta en cascade*/


/**Actualizar desvios y retrasos al poner Incidencias**/
SET SERVEROUTPUT ON

CREATE or REPLACE TRIGGER NEWRETRASO
AFTER INSERT ON INCIDENCIA
FOR EACH ROW
WHEN (new.tipo = 'retrasado')
  BEGIN
  INSERT INTO RETRASO(id) VALUES (:new.id);
  DBMS_OUTPUT.put_line ('Ahora se debería especificar el tiempo de retraso');
END NEWRETRASO;
/

CREATE or REPLACE TRIGGER NEWDESVIO
AFTER INSERT ON INCIDENCIA
FOR EACH ROW
WHEN ((new.tipo = 'desviado1') OR (new.tipo = 'desviado2'))
  BEGIN
  INSERT INTO DESVIO(id) VALUES (:new.id);
  DBMS_OUTPUT.put_line ('Ahora se debería especificar el nuevo avion y nuevo aeropuerto');
END NEWDESVIO;
/
