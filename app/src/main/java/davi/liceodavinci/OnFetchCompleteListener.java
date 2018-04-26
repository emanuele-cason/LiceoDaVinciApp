package davi.liceodavinci;

/**
 * Created by Emanuele on 26/04/2018 at 14:32!
 */

public interface OnFetchCompleteListener<T> {
    public void onSuccess(T result);
    public void onFailure(Exception e);
}
