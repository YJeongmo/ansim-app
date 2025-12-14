package com.example.coderelief.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MarkerLabelGenerator {
    
    private final Context context;
    private final float density;
    
    public MarkerLabelGenerator(Context context) {
        this.context = context;
        this.density = context.getResources().getDisplayMetrics().density;
    }
    
    /**
     * 빨간색 마커 위에 장소명 라벨을 생성합니다.
     * 
     * @param text 표시할 텍스트
     * @return BitmapDescriptor
     */
    public BitmapDescriptor createMarkerWithLabel(String text) {
        // 텍스트 길이 제한 (너무 길면 잘라내기)
        String displayText = text;
        if (displayText.length() > 10) {
            displayText = displayText.substring(0, 10) + "...";
        }
        
        // 텍스트 페인트 설정
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(12 * density); // 12sp
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        // 배경 페인트 설정
        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#FF5252")); // 빨간색
        backgroundPaint.setStyle(Paint.Style.FILL);
        
        // 테두리 페인트 설정
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1.5f * density);
        
        // 텍스트 크기 측정
        Rect textBounds = new Rect();
        textPaint.getTextBounds(displayText, 0, displayText.length(), textBounds);
        
        // 라벨 크기 계산
        int padding = (int) (6 * density);
        int labelWidth = textBounds.width() + (padding * 2);
        int labelHeight = textBounds.height() + (padding * 2);
        
        // 마커 크기 (Google Maps 기본 마커 크기 참고)
        int markerSize = (int) (24 * density);
        int markerBottomOffset = (int) (8 * density); // 마커 아래 여유 공간
        
        // 전체 비트맵 크기 계산
        int totalWidth = Math.max(labelWidth, markerSize);
        int totalHeight = labelHeight + markerSize + markerBottomOffset;
        
        // 비트맵 생성
        Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // 라벨 배경 그리기 (둥근 모서리)
        float labelLeft = (totalWidth - labelWidth) / 2f;
        float labelTop = 0;
        RectF labelRect = new RectF(labelLeft, labelTop, labelLeft + labelWidth, labelTop + labelHeight);
        float cornerRadius = 4 * density;
        canvas.drawRoundRect(labelRect, cornerRadius, cornerRadius, backgroundPaint);
        canvas.drawRoundRect(labelRect, cornerRadius, cornerRadius, borderPaint);
        
        // 텍스트 그리기
        float textX = totalWidth / 2f;
        float textY = labelTop + (labelHeight / 2f) + (textBounds.height() / 2f);
        canvas.drawText(displayText, textX, textY, textPaint);
        
        // 마커 점 그리기 (라벨 아래)
        Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(Color.parseColor("#FF5252")); // 빨간색
        markerPaint.setStyle(Paint.Style.FILL);
        
        Paint markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerBorderPaint.setColor(Color.WHITE);
        markerBorderPaint.setStyle(Paint.Style.STROKE);
        markerBorderPaint.setStrokeWidth(2 * density);
        
        float markerCenterX = totalWidth / 2f;
        float markerCenterY = labelHeight + (markerSize / 2f);
        float markerRadius = markerSize / 2f;
        
        canvas.drawCircle(markerCenterX, markerCenterY, markerRadius, markerPaint);
        canvas.drawCircle(markerCenterX, markerCenterY, markerRadius, markerBorderPaint);
        
        // 마커 중심점 (작은 원)
        Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.WHITE);
        centerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(markerCenterX, markerCenterY, markerRadius / 3f, centerPaint);
        
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    
    /**
     * 파란색 마커(현재 위치)를 생성합니다.
     * 
     * @return BitmapDescriptor
     */
    public BitmapDescriptor createCurrentLocationMarker() {
        int markerSize = (int) (24 * density);
        
        Bitmap bitmap = Bitmap.createBitmap(markerSize, markerSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // 외곽 원 (파란색)
        Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setColor(Color.parseColor("#2196F3")); // 파란색
        outerPaint.setStyle(Paint.Style.FILL);
        
        Paint outerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerBorderPaint.setColor(Color.WHITE);
        outerBorderPaint.setStyle(Paint.Style.STROKE);
        outerBorderPaint.setStrokeWidth(2 * density);
        
        float centerX = markerSize / 2f;
        float centerY = markerSize / 2f;
        float radius = markerSize / 2f;
        
        canvas.drawCircle(centerX, centerY, radius, outerPaint);
        canvas.drawCircle(centerX, centerY, radius, outerBorderPaint);
        
        // 중심점 (작은 원)
        Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.WHITE);
        centerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius / 3f, centerPaint);
        
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}

