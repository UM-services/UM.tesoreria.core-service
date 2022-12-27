/**
 * 
 */
package ar.edu.um.tesoreria.rest.util;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Periodo {

	private Integer anho;
	private Integer mes;

	public Long toPeriodo() {
		return (long) this.anho * 100 + this.mes;
	}

	public void next() {
		if (++mes == 13) {
			anho++;
			mes = 1;
		}
	}

	public String periodoKey() {
		return this.anho + "." + this.mes;
	}

	public static Periodo parse(long periodo) {
		Integer anho = (int) (periodo / 100);
		Integer mes = (int) (periodo - anho * 100);
		return new Periodo(anho, mes);
	}

	public static Periodo next_month(Integer anho, Integer mes) {
		if (++mes == 13) {
			anho++;
			mes = 1;
		}
		return new Periodo(anho, mes);
	}

	public static Periodo next_periodo(long periodo) {
		Periodo p = parse(periodo);
		return next_month(p.getAnho(), p.getMes());
	}

	public static Periodo prev_month(Integer anho, Integer mes) {
		if (--mes == 0) {
			anho--;
			mes = 12;
		}
		return new Periodo(anho, mes);
	}

	public static Integer diffMonth(Periodo from, Periodo to) {
		if (to.toPeriodo() < from.toPeriodo()) {
			return 0;
		}
		if (from.getAnho() == to.getAnho()) {
			return to.getMes() - from.getMes();
		}
		Integer meses = (to.getAnho() - from.getAnho()) * 12;
		meses += to.getMes();
		meses -= from.getMes();
		return meses;
	}

	public static List<Periodo> makePeriodos(Long periododesde, Long periodohasta) {
		List<Periodo> periodos = new ArrayList<Periodo>();
		Long ciclo = periododesde;
		while (ciclo <= periodohasta) {
			Periodo periodo = null;
			periodos.add(periodo = Periodo.parse(ciclo));
			periodo = Periodo.next_month(periodo.getAnho(), periodo.getMes());
			ciclo = periodo.toPeriodo();
		}
		return periodos;
	}

}
