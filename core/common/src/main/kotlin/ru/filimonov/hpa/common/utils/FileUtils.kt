package ru.filimonov.hpa.common.utils

import java.net.URI

fun URI.isLocalResource() = scheme == "file" || scheme == "content"

fun URI.isContentResource() = scheme == "content"
