-- TRAINING_HISTORYテーブルのcalories_consumedカラムをDOUBLE型に変更
ALTER TABLE TRAINING_HISTORY 
MODIFY COLUMN calories_consumed DOUBLE COMMENT '消費カロリー';