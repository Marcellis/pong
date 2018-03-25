
package mangavalk.canvasfirsttry;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;

public class MainActivity extends Activity
{
    CanvasView canvasClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canvasClass = new CanvasView(this);
        setContentView(canvasClass);
        mActivePointers = new SparseArray<PointF>();
        canvasClass.SetActivity(this);
    }

    private SparseArray<PointF> mActivePointers;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        // get pointer index from the event object
        int pointerIndex = e.getActionIndex();

        // get pointer ID
        int pointerId = e.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = e.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers

                PointF f = new PointF();
                f.x = e.getX(pointerIndex);
                f.y = e.getY(pointerIndex);
                mActivePointers.put(pointerId, f);

                for (int size = e.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(e.getPointerId(i));
                    if (point != null) {
                        point.x = e.getX(i);
                        point.y = e.getY(i);
                        canvasClass.sendMove(point.x, point.y);
                    }
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = e.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(e.getPointerId(i));
                    if (point != null) {
                        point.x = e.getX(i);
                        point.y = e.getY(i);
                        canvasClass.sendMove(point.x, point.y);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_CANCEL: {
                // TODO use data
                break;
            }
        }


        return true;
    }

    @Override
    public void onBackPressed()
    {
        canvasClass.SetGameState(false);
    }
}
