package math;

public interface IFormatter {

	/** @return the Object formatted by the Default Format of this Formatter */
	public String format(Object arg);

	/** @return the Object formatted by the given Format */
	public String format(Object arg, String Format);

}
