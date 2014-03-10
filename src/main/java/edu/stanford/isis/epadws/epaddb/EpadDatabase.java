package edu.stanford.isis.epadws.epaddb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

import edu.stanford.isis.epad.common.util.EPADLogger;
import edu.stanford.isis.epad.common.util.EPADTools;

public class EpadDatabase
{
	private static EPADLogger logger = EPADLogger.getInstance();

	private static final EpadDatabase ourInstance = new EpadDatabase();

	private ConnectionPool connectionPool;
	private EpadDatabaseOperations epadDatabaseOperations;

	private final AtomicReference<DatabaseState> dbState = new AtomicReference<DatabaseState>(DatabaseState.INIT);

	private long startupTime = -1;

	public static EpadDatabase getInstance()
	{
		return ourInstance;
	}

	private EpadDatabase()
	{
		initConnectionPool();
	}

	public void startup()
	{
		try {
			dbState.set(DatabaseState.STARTING);
			long time = System.currentTimeMillis();

			if (!tablesUpToDate()) {
				logger.info("IMPORTANT: NEED to add the epaddb tables using MySQL command-line!!");
			} else {
				logger.info("ePad's extra MySQL tables appear to be up to date.");
			}
			startupTime = System.currentTimeMillis() - time;
			dbState.set(DatabaseState.READY);
			logger.info("Database took " + startupTime + " ms to start.");
		} catch (Exception e) {
			logger.severe("Failed to start-up database", e);
			dbState.set(DatabaseState.ERROR);
		}
	}

	public long getStartupTime()
	{
		return startupTime;
	}

	public void shutdown()
	{
		dbState.set(DatabaseState.SHUTDOWN);
		long time = System.currentTimeMillis();
		logger.info("Shutting down database.");

		closeConnectionPool();
		logger.info("The database took " + (System.currentTimeMillis() - time) + " ms, to shutdown.");
	}

	public EpadDatabaseOperations getEPADDatabaseOperations()
	{
		return epadDatabaseOperations;
	}

	public int getConnectionPoolAvailCount()
	{
		return connectionPool.availableConnectionCount();
	}

	public int getConnectionPoolUsedCount()
	{
		return connectionPool.usedConnectionCount();
	}

	private void initConnectionPool()
	{
		try {
			logger.info("Creating connection pool.");
			createConnectionPool();
			epadDatabaseOperations = new DefaultEpadDatabaseOperations(connectionPool);
		} catch (Exception e) {
			logger.severe("Failed to create connection pool", e);
			dbState.set(DatabaseState.ERROR);
		}
	}

	private void createConnectionPool() throws SQLException
	{
		String username = EPADTools.epadDatabaseUsername;
		String password = EPADTools.epadDatabasePassword;
		String epadDatabaseURL = EPADTools.epadDatabaseURL;

		logger.info("MySql using connection string for ePAD database: " + epadDatabaseURL);

		connectionPool = new ConnectionPool(epadDatabaseURL, username, password);
	}

	private void closeConnectionPool()
	{
		connectionPool.dispose();
	}

	private boolean tablesUpToDate()
	{
		boolean result = false;
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Checking MySQL database.");
			conn = connectionPool.getConnection();
			s = conn.createStatement();

			try {
				rs = s.executeQuery("SELECT version FROM epaddb.dbversion");
			} catch (Exception e) {
				logger.info("SELECT version FROM epaddb.dbversion had error: " + e.getMessage());
				return false;
			}
			if (rs.next()) {
				result = true;
				sb.append("DB Version is: ").append(rs.getString("version")).append(" ");
			}
		} catch (SQLException sqle) {
			logger.warning("SQL Error when checking for database version", sqle);
			result = false;
		} finally {
			logger.info(sb.toString());
			DatabaseUtils.close(rs);
			DatabaseUtils.close(s);
			DatabaseUtils.close(conn);
		}
		return result;
	}
}
