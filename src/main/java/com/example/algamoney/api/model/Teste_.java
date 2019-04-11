package com.example.algamoney.api.model;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Teste.class)
public abstract class Teste_ {

	public static volatile SingularAttribute<Teste, Long> codigo;
	public static volatile SingularAttribute<Teste, LocalDateTime> dataHora;

	public static final String CODIGO = "codigo";
	public static final String DATA_HORA = "dataHora";

}

