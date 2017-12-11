package com.example.cukier.shemboard;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Point;
import android.inputmethodservice.InputMethodService;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.ajwerner.voronoi.*;

import java.util.*;

public class ShemboardService extends InputMethodService
{
    private KeyMap.Map map;
    private LinearLayout inputView;
    private boolean[] previouslyPressedButtons = new boolean[5];
    private ImageView imageView6;
    private Canvas canvas6;
    private Bitmap bitmap6;
    private List<Point> fingerPoints;
    int NOTHING_SPECIAL_MODE = 0;
    int SPECIAL_MODE_1 = 1;
    private int specialMode;
    
    public ShemboardService()
    {
        map = KeyMap.Map.ABDHP;
        resetButtonPresses();
        fingerPoints = new ArrayList<>();
        
    }
    
    private void resetButtonPresses()
    {
        for (int i = 0; i < 5; i++)
        {
            previouslyPressedButtons[i] = false;
        }
        
        specialMode = NOTHING_SPECIAL_MODE;
    }
    
    @Override
    public View onCreateInputView()
    {
        inputView = (LinearLayout) getLayoutInflater().inflate(
                R.layout.input, null);
        this.setupLayout();
        
        
        return inputView;
    }
    
    @Override
    public void onComputeInsets(Insets outInsets)
    {
        super.onComputeInsets(outInsets);
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        outInsets.visibleTopInsets = height;
        outInsets.contentTopInsets = height;
        outInsets.touchableInsets = height;
    }
    
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting)
    {
        super.onStartInputView(info, restarting);
        
        fingerPoints.clear(); //TODO consider removing
        
        //Set size to maximum screen
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        
        ViewGroup.LayoutParams params = inputView.getLayoutParams();
        params.height = height;
        inputView.setLayoutParams(params);
        
        //Image view
        imageView6 = inputView.findViewById(R.id.imageView6);
        //create a new canvas with a transparent background
        bitmap6 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        canvas6 = new Canvas(bitmap6);
        drawStarterCanvasImage();
        updateCanvas();
    }
    
    private void setupLayout()
    {
        inputView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return ShemboardService.this.onTouch(view, motionEvent);
            }
        });
        
        inputView.setLongClickable(true);
    }
    
    private static int[] fingerPaintColorIDs = new int[]{
            R.color.fingerPointColor1,
            R.color.fingerPointColor2,
            R.color.fingerPointColor3,
            R.color.fingerPointColor4,
            R.color.fingerPointColor5};
    
    private boolean onTouch(View view, MotionEvent motionEvent)
    {
        int action = motionEvent.getActionMasked();
        int pointerIndex = motionEvent.getActionIndex();
        
        Point touchPlace = new Point();
        touchPlace.set((int) motionEvent.getX(pointerIndex), (int) motionEvent.getY(pointerIndex));
        
        if (fingerPoints.size() < 5)
        {
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            {
                fingerPoints.add(touchPlace);
                workYourVoronoiMagic();
            }
        }
        else
        {
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            {
                //Find closest point
                int closestPointIndex = 0;
                double sqrClosestDistance = Double.POSITIVE_INFINITY;
                for (int i = 0; i < fingerPoints.size(); i++)
                {
                    Point fingerPoint = fingerPoints.get(i);
                    double sqrDistance = (fingerPoint.x - touchPlace.x) * (fingerPoint.x - touchPlace.x)
                            + (fingerPoint.y - touchPlace.y) * (fingerPoint.y - touchPlace.y);
                    if (sqrDistance < sqrClosestDistance)
                    {
                        sqrClosestDistance = sqrDistance;
                        closestPointIndex = i;
                    }
                }
                
                previouslyPressedButtons[closestPointIndex] = true;
                
                Log.d("press", pointerIndex + " " + MotionEvent.actionToString(action));
                Log.d("btns", Arrays.toString(previouslyPressedButtons));
            }
            
            if (action == MotionEvent.ACTION_UP)
            {
                
                Log.d("press", action + " " + MotionEvent.actionToString(action));
                onAllButtonsReleased();
            }
        }
        return false;
    }
    
    private void drawStarterCanvasImage()
    {
        canvas6.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        //getResources().getDrawable(R.drawable.background, null).draw(canvas6);
        
        //stupid effect
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        Paint paint = new Paint();
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);
        for (int k = 0; k < 5; k++)
        {
            path.reset();
            path.moveTo(width / 2, height / 2);
            for (int i = 0; i < width; i += 5)
            {
                double angle = (float) i / width * Math.PI * 19.654567 + (k * 2 * Math.PI / 5);
                double radius = 2 * i;
                float x = (float) (width / 2 + Math.cos(angle) * radius);
                float y = (float) (height / 2 + Math.sin(angle) * radius);
                path.lineTo(x, y);
            }
            paint.setColor(getResources().getColor(fingerPaintColorIDs[k], null));
            canvas6.drawPath(path, paint);
        }
        
        updateCanvas();
    }
    
    private void workYourVoronoiMagic()
    {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int extra = 0; //for debugging
        int minX = 0 + extra;
        int minY = 0 + extra;
        int maxX = displayMetrics.widthPixels - extra;
        int maxY = displayMetrics.heightPixels - extra;
        
        ArrayList<Site> sites = new ArrayList<>();
        for (Point p : fingerPoints)
        {
            sites.add(new Site(p.x, p.y));
        }
        
        //Voronoi calculation
        Voronoi vore = new Voronoi(sites, minX, minY, maxX, maxY);
        List<VoronoiEdge> edgeList = vore.edgeList;
        
        //Clamp to screen rect
        ArrayList<ScreenPoint> extraPointsToAdd = new ArrayList<>();
        for (VoronoiEdge edge : edgeList)
        {
            clampVoronoiEdgeAndAddPoint(edge, extraPointsToAdd, minX, minY, maxX, maxY);
        }
        
        //Add four corners as points
        int halfWidth = (maxX - minX) / 2;
        int halfHeight = (maxY - minY) / 2;
        addCornerPoints(sites, extraPointsToAdd, minX, minY, maxX, maxY);
        
        //Sort all outer points by angle from center
        ScreenPoint screenCenter = new ScreenPoint(minX + halfWidth, minY + halfHeight, null);
        Map<ScreenPoint, Double> outerPointsKeys = new HashMap<>();
        for (ScreenPoint point : extraPointsToAdd)
        {
            outerPointsKeys.put(point, angleFromTo(screenCenter, point));
        }
        Collections.sort(extraPointsToAdd, (s1, s2) -> (outerPointsKeys.get(s1) - outerPointsKeys.get(s2)) > 0 ? 1 : -1);
        //Connect all outer points in a circular fashion, creating the screen rectangle lines
        int n_ex = extraPointsToAdd.size();
        for (int i = 0; i < n_ex; i++)
        {
            ScreenPoint p1 = extraPointsToAdd.get((i) % n_ex);
            ScreenPoint p2 = extraPointsToAdd.get((i + 1) % n_ex);
            org.ajwerner.voronoi.Point site = null;
            if (p1.edge.site1 != null)
            {
                if (p1.edge.site1.equals(p2.edge.site1))
                    site = p1.edge.site1;
                else if (p1.edge.site1.equals(p2.edge.site2))
                    site = p1.edge.site1;
            }
            if (p1.edge.site2 != null)
            {
                if (p1.edge.site2.equals(p2.edge.site1))
                    site = p1.edge.site2;
                else if (p1.edge.site2.equals(p2.edge.site2))
                    site = p1.edge.site2;
            }
            if (site == null)
            {
                site = p1.edge.site1;
                Log.e("Voronoi post-algorithm", "Bad ordering of the points" + p1 + "     " + p2);
            }
            if (site instanceof Site)
            {
                VoronoiEdge edge = new VoronoiEdge(site);
                edge.p1 = new org.ajwerner.voronoi.Point(p1.x, p1.y);
                edge.p2 = new org.ajwerner.voronoi.Point(p2.x, p2.y);
                edgeList.add(edge);
            }
            else
            {
                Log.e("Something bad", "is going on    " + p1 + "  " + p2);
            }
        }
        
        //Collect nearby lines for each finger point
        Map<Site, List<VoronoiEdge>> linesOfPoints = new HashMap<>();
        for (Site site : sites)
        {
            linesOfPoints.put(site, new ArrayList<>());
        }
        for (VoronoiEdge edge : edgeList)
        {
            if (edge.site1 instanceof Site)
            {
                Site site = (Site) edge.site1;
                linesOfPoints.get(site).add(edge);
            }
            
            if (edge.site2 instanceof Site)
            {
                Site site = (Site) edge.site2;
                linesOfPoints.get(site).add(edge);
            }
            
            if (!(edge.site1 instanceof Site) && !(edge.site2 instanceof Site))
            {
                Log.e("Voronoi post-algo", "That's not a line I expected! " + edge);
            }
        }
        
        canvas6.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawAreas(sites, linesOfPoints);
        if (fingerPoints.size() < 5) //TODO
            drawEdges(edgeList);
    }
    
    private void drawAreas(List<Site> sites, Map<Site, List<VoronoiEdge>> linesOfPoints)
    {
        //Draw areas for each finger point
        for (int siteIndex = 0; siteIndex < sites.size(); siteIndex++)
        {
            Site site = sites.get(siteIndex);
            ScreenPoint sitePoint = new ScreenPoint(site.x, site.y);
            
            //Remove duplicates and sort circularly and connect
            Set<ScreenPoint> pointsSet = new HashSet<>();
            for (VoronoiEdge edge : linesOfPoints.get(site))
            {
                pointsSet.add(new ScreenPoint(edge.p1));
                pointsSet.add(new ScreenPoint(edge.p2));
            }
            Map<ScreenPoint, Double> keys = new HashMap<>();
            for (ScreenPoint point : pointsSet)
            {
                keys.put(point, angleFromTo(sitePoint, point));
            }
            List<ScreenPoint> points = new ArrayList<>(pointsSet);
            Collections.sort(points, (p1, p2) -> (keys.get(p1) - keys.get(p2)) > 0 ? 1 : -1);
            
            int n_points = points.size();
            if (n_points > 1)
            {
                Path path = new Path();
                
                path.moveTo(points.get(0).x, points.get(0).y);
                for (int i = 1; i < n_points; i++)
                {
                    ScreenPoint next = points.get(i);
                    path.lineTo(next.x, next.y);
                }
                path.lineTo(points.get(0).x, points.get(0).y);
                
                path.setFillType(Path.FillType.EVEN_ODD);
                Paint fillPaint = new Paint();
                fillPaint.setStyle(Paint.Style.FILL);
                fillPaint.setColor(getResources().getColor(fingerPaintColorIDs[siteIndex], null));
                canvas6.drawPath(path, fillPaint);
            }
            else
                Log.e("No Draw :(", "" + n_points + "  " + points.size() + "    " + linesOfPoints.get(site).size());
        }
        
        updateCanvas();
    }
    
    private void drawEdges(List<VoronoiEdge> edgeList)
    {
        //Draw inner lines, thick then thin
        int n = edgeList.size();
        float[] linesToDraw = new float[n * 4];
        for (int i = 0; i < n; i++)
        {
            VoronoiEdge edge = edgeList.get(i);
            linesToDraw[i * 4] = (float) edge.p1.x;
            linesToDraw[i * 4 + 1] = (float) edge.p1.y;
            linesToDraw[i * 4 + 2] = (float) edge.p2.x;
            linesToDraw[i * 4 + 3] = (float) edge.p2.y;
        }
        Paint linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.fingerPointLineColorThick));
        linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        linePaint.setStrokeWidth(40.123f);
        canvas6.drawLines(linesToDraw, linePaint);
        updateCanvas();
    }
    
    private double angleFromTo(ScreenPoint center, ScreenPoint other)
    {
        return Math.atan2(other.y - center.y, other.x - center.x);
    }
    
    private void addCornerPoints(ArrayList<Site> sites, ArrayList<ScreenPoint> points, int minX, int minY, int maxX, int maxY)
    {
        for (int i = 0; i < 4; i++)
        {
            ScreenPoint corner;
            if (i == 0)
                corner = new ScreenPoint(minX, minY);
            else if (i == 1)
                corner = new ScreenPoint(minX, maxY);
            else if (i == 2)
                corner = new ScreenPoint(maxX, minY);
            else
                corner = new ScreenPoint(maxX, maxY);
            double minSquareDistance = Double.MAX_VALUE;
            Site closestSite = null;
            for (Site site : sites)
            {
                double sqrDistanceToThatPoint = site.sqrDistanceTo(corner);
                if (sqrDistanceToThatPoint < minSquareDistance)
                {
                    minSquareDistance = sqrDistanceToThatPoint;
                    closestSite = site;
                }
            }
            VoronoiEdge edge = new VoronoiEdge(closestSite);
            //Adding corner to list. It's fine that other corners will check it, they won't be closest, probably.
            points.add(new ScreenPoint(corner.x, corner.y, edge));
        }
    }
    
    private void clampVoronoiEdgeAndAddPoint(VoronoiEdge edge, List<ScreenPoint> points,
                                             int minX, int minY, int maxX, int maxY)
    {
        boolean changed1 = false;
        if (edge.p1.x <= minX || edge.p1.x >= maxX)
        {
            int newX = (edge.p1.x <= minX) ? minX : maxX;
            
            int newY = (int) (edge.intercept + edge.slope * newX);
            edge.p1 = new org.ajwerner.voronoi.Point(newX, newY);
            changed1 = true;
        }
        if (edge.p1.y <= minY || edge.p1.y >= maxY)
        {
            int newY = (edge.p1.y <= minY) ? minY : maxY;
            int newX;
            if (edge.isVertical)
            {
                //vertical line - unchanged x
                newX = (int) edge.p1.x;
            }
            else
            {
                //y=mx+b therefore x=(y-b)/m
                newX = (int) ((newY - edge.intercept) / edge.slope);
            }
            edge.p1 = new org.ajwerner.voronoi.Point(newX, newY);
            changed1 = true;
        }
        
        if (changed1)
            points.add(new ScreenPoint(edge.p1, edge));
        
        //copy
        //paste
        //replace 1 with 2
        
        boolean changed2 = false;
        if (edge.p2.x <= minX || edge.p2.x >= maxX)
        {
            int newX = (edge.p2.x <= minX) ? minX : maxX;
            
            int newY = (int) (edge.intercept + edge.slope * newX);
            edge.p2 = new org.ajwerner.voronoi.Point(newX, newY);
            changed2 = true;
        }
        if (edge.p2.y <= minY || edge.p2.y >= maxY)
        {
            int newY = (edge.p2.y <= minY) ? minY : maxY;
            int newX;
            if (edge.isVertical)
            {
                //vertical line - unchanged x
                newX = (int) edge.p2.x;
            }
            else
            {
                //y=mx+b therefore x=(y-b)/m
                newX = (int) ((newY - edge.intercept) / edge.slope);
            }
            edge.p2 = new org.ajwerner.voronoi.Point(newX, newY);
            changed2 = true;
        }
        
        if (changed2)
            points.add(new ScreenPoint(edge.p2, edge));
    }
    
    private void updateCanvas()
    {
        imageView6.setImageBitmap(bitmap6);
    }
    
    private void onAllButtonsReleased()
    {
        
        char character = '&';
        int binary = 0;
        for (int i = 0; i < 5; i++)
            if (previouslyPressedButtons[i])
                binary += 1 << i; // 2 to the power of i
        if (binary <= 0)
        {
            Log.e("decoded 0", "no! why?");
            resetButtonPresses();
            return;
        }
        
        if (binary >= 32)
            Log.d("Binary", "ERROR: binary number too large: " + binary);
        else
            character = map.decode(binary);
        
        if (specialMode == 0) //no special char
        {
            if (character == '#') //None
            {
                getCurrentInputConnection().commitText("#", 1);
            }
            else if (character == '@') //Exit keyboard
            {
                //TEMP: switch to heb/eng TODO
                if (map == KeyMap.Map.ABDHP)
                    map = KeyMap.Map.אבגדה;
                else if (map == KeyMap.Map.אבגדה)
                    map = KeyMap.Map.ETAOI;
                else if (map == KeyMap.Map.ETAOI)
                    map = KeyMap.Map.ABDHP;
            }
            else if (character == '-') //Backspace
            {
                getCurrentInputConnection().deleteSurroundingText(1, 0);
            }
            else if (character == '$') //Special
            {
                specialMode = SPECIAL_MODE_1;
            }
            else //Normal character
            {
                getCurrentInputConnection().commitText("" + character, 1);
            }
        }
        
        resetButtonPresses();
    }
    
}
