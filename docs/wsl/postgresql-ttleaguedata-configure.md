```sql
-- Create a new database
CREATE DATABASE ttleaguedata;

-- Create a new user
CREATE USER ttleagueuser WITH PASSWORD 'ttleaguepass';
GRANT ALL PRIVILEGES ON DATABASE ttleaguedata TO ttleagueuser;

-- Exit
\q
```