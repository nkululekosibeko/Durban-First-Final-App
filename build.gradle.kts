// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")  // Added Google services classpath
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
}
