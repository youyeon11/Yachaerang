ALTER TABLE daily_price
    ADD COLUMN price_change BIGINT NULL, -- 전일 대비 가격 변화량
ADD COLUMN price_change_rate DECIMAL(7,2); -- 전일 대비 가격 변화율

ALTER TABLE weekly_price
    ADD COLUMN price_change BIGINT NULL, -- 가격 변화량
ADD COLUMN price_change_rate DECIMAL(7,2); -- 가격 변화율

ALTER TABLE monthly_price
    ADD COLUMN price_change BIGINT NULL, -- 가격 변화량
ADD COLUMN price_change_rate DECIMAL(7,2); -- 가격 변화율

ALTER TABLE yearly_price
    ADD COLUMN price_change BIGINT NULL, -- 가격 변화량
ADD COLUMN price_change_rate DECIMAL(7,2); -- 가격 변화율
