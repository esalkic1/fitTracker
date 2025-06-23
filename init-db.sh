psql -v ON_ERROR_STOP=1 --username "postgres" <<-EOSQL
    CREATE DATABASE nwt_auth;
    CREATE DATABASE nwt_events;
    CREATE DATABASE nwt_notification;
    CREATE DATABASE nwt_nutrition;
    CREATE DATABASE nwt_workout;
EOSQL