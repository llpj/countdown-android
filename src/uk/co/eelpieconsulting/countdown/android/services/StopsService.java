package uk.co.eelpieconsulting.countdown.android.services;

import java.util.List;

import uk.co.eelpieconsulting.buses.client.exceptions.HttpFetchException;
import uk.co.eelpieconsulting.buses.client.exceptions.ParsingException;
import uk.co.eelpieconsulting.busroutes.model.Stop;
import uk.co.eelpieconsulting.countdown.android.api.BusesClientService;
import uk.co.eelpieconsulting.countdown.android.services.caching.StopsCache;
import uk.co.eelpieconsulting.countdown.android.services.network.NetworkNotAvailableException;
import android.util.Log;

public class StopsService {
	
	private static final String TAG = "StopsService";
	
	private final BusesClientService busesClientService;
	private final StopsCache stopsCache;
	
	public StopsService(BusesClientService busesClientService, StopsCache stopsCache) {
		this.busesClientService = busesClientService;
		this.stopsCache = stopsCache;		
	}

	public List<Stop> getRouteStops(String route, int run) throws ContentNotAvailableException {		
		try {
			final List<Stop> cachedResults = stopsCache.getRouteStops(route, run);
			final boolean cachedResultsAreAvailable = cachedResults != null;
			if (cachedResultsAreAvailable) {
				Log.i(TAG, "Returning route stops from cache");
				return cachedResults;
			}
			
			final List<Stop> stops = busesClientService.getRouteStops(route, run);
			stopsCache.cacheStops(route, run, stops);
			return stops;
			
		} catch (NetworkNotAvailableException e) {
			throw new ContentNotAvailableException(e);
		} catch (HttpFetchException e) {
			throw new ContentNotAvailableException(e);		
		} catch (ParsingException e) {
			throw new ContentNotAvailableException(e);
		}		
	}

	public List<Stop> findStopsWithin(double latitude, double longitude, int radius) throws ContentNotAvailableException {
		try {
			final List<Stop> cachedResults = stopsCache.getStopsWithin(latitude, latitude, radius);
			final boolean cachedResultsAreAvailable = cachedResults != null;
			if (cachedResultsAreAvailable) {
				Log.i(TAG, "Returning route stops from cache");
				return cachedResults;
			}
			
			final List<Stop> stops = busesClientService.findStopsWithin(latitude, longitude, radius);
			stopsCache.cacheStops(latitude, longitude, radius, stops);
			return stops;
			
		} catch (NetworkNotAvailableException e) {
			throw new ContentNotAvailableException(e);
		} catch (HttpFetchException e) {
			throw new ContentNotAvailableException(e);		
		} catch (ParsingException e) {
			throw new ContentNotAvailableException(e);
		}		
	}

}
