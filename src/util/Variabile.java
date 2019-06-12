package util;

import java.io.Serializable;

public enum Variabile implements Serializable{ 
	NUMERO_VEICOLI(100,"NUM. VEICOLI"), 
	PERIODO_DI_GENERAZIONE(-500,"TEMPO DI GENERAZIONE");
	
	private double incremento;
	private String nome;
	
	private Variabile(double incremento, String nome) {
		this.incremento = incremento;
		this.nome = nome;
	}
	public double getIncremento() {return incremento;}
	public String getNome() {return nome;}
}
