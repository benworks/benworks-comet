/**
 * 
 */
package benworks.comet.broadcast;

/**
 * controller接口
 * 
 */
public interface GeneralController {

	GeneralResponse handle(GeneralRequest request) throws Exception;
}
