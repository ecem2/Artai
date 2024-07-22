package com.hc.artai.data.model.output

data class Input(
    val guidance_scale: Double,
    val height: Int,
    val num_inference_steps: Int,
    val num_outputs: Int,
    val prompt: String,
    val scheduler: String,
    val width: Int
)