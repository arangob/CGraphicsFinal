package finalproject;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.management.RuntimeErrorException;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class SoundFile {

	private static boolean initializedOpenAL = false;
	private static long openALContext = 0;
	private static long openALDevice = 0; 
	
	private static int dataBuffer;
	private static float streamLength;
	private static int sourcePointer;
	private static int sampleRate;
	private static int channels;

	private static void initializeOpenAL() {
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		openALDevice = alcOpenDevice(defaultDeviceName);
		
		int[] attributes = {0};
		long context = alcCreateContext(openALDevice, attributes);
		alcMakeContextCurrent(context);
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(openALDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		initializedOpenAL = true;		
		// TODO: error checking
	}
	
	public static void teardownOpenAL() {
		// TODO: ref count and auto-call, then make private
		if (initializedOpenAL) {
            alcCloseDevice(openALDevice);
            try {
            	alcDestroyContext(openALContext);
            } catch (NullPointerException e) {
				// Blah, not sure why alcDestroyContext() throws a null pointer
            	// exception sometimes, but not enough time to figure out what's
            	// going on. Just eating the exception, as per:
            	// https://github.com/libgdx/libgdx/issues/2578
			}
			initializedOpenAL = false;
		}
	}
	
	public SoundFile(String filename) {
		if (!initializedOpenAL) {
			initializeOpenAL();
		}
		
		//Allocate space to store return information from the function
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);

		ShortBuffer rawAudioBuffer = null;
		if (filename.endsWith(".ogg")) {
			int[] stb_error = new int[1];
			long vorbisFile = stb_vorbis_open_filename(filename, stb_error, null);
			streamLength = stb_vorbis_stream_length_in_seconds(vorbisFile);
			stb_vorbis_close(vorbisFile);
		
			rawAudioBuffer = stb_vorbis_decode_filename(filename, channelsBuffer, sampleRateBuffer);
		} else if (filename.endsWith(".mp3")) {
			
		} else {
			throw new RuntimeException("Unsupported audio format");
		}
		
		//Retreive the extra information that was stored in the buffers by the function
		channels = channelsBuffer.get();
		sampleRate = sampleRateBuffer.get();
		//Free the space we allocated earlier
		stackPop();
		stackPop();

		
		//Find the correct OpenAL format
		int format = -1;
		if(channels == 1) {
		    format = AL_FORMAT_MONO16;
		} else if(channels == 2) {
		    format = AL_FORMAT_STEREO16;
		}

		//Request space for the buffer
		dataBuffer = alGenBuffers();

		//Send the data to OpenAL
		alBufferData(dataBuffer, format, rawAudioBuffer, sampleRate);

		//Free the memory allocated by STB
		free(rawAudioBuffer);

		
		sourcePointer = alGenSources();

	}
	
	public float getLength() {
		return streamLength;
	}
	
	public void stop() {
		alSourceStop(sourcePointer);
	}
	
	public void play() {
		play(false);
	}

	public void play(boolean blocking) {

		stop();

		//Assign our buffer to the source
		alSourcei(sourcePointer, AL_BUFFER, dataBuffer);

		alSourcePlay(sourcePointer);
		
		if (blocking) {
			try {
				Thread.sleep((int)(streamLength*1000.0));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public float getPlayingTime() {
		// TODO: clean up sourcePointer usage and prevent multi-play
		if (alGetSourcei(sourcePointer, AL_SOURCE_STATE) == AL_PLAYING) {	
			return alGetSourcei(sourcePointer, AL_SAMPLE_OFFSET)/(float)sampleRate;
	    } else {
	    	return streamLength;
	    }
	}
	
	public void destroy() {
		// todo: auto call al teardown?
		alDeleteSources(sourcePointer);
		alDeleteBuffers(dataBuffer);
	}
}
