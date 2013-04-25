package benworks.comet.broadcast;

import java.io.Serializable;

/**
 * @author benworks
 * 
 */
public interface ResponseMessage extends Serializable {

	int getSerial();

	void setSerial(int serial);
}
